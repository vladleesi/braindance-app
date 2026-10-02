# Cloudflare in front of Cloud Run

Cloudflare DNS/CDN → existing Cloud Run backend. No Workers or new paid services.
Keep minimum instances **0** and preserve CPU, memory, CPU Boost, billing mode, scaling, and existing security controls.
Examples use placeholders; keep actual hostnames, identifiers, and secrets in ignored private configuration.

## Cache policy

Shared caching is disabled by default (`PUBLIC_CACHE_ENABLED=false`); the origin guard is unset until configured.

| Response | Cache-Control |
| --- | --- |
| Explicitly public, anonymous `GET /v1/giveaways`, HTTP 200, verified HTTPS host | `public, max-age=60, stale-while-revalidate=30, stale-if-error=0` |
| Everything else | `private, no-store` |

All queries, Origin/credential/identity headers, other paths, and non-GET methods—including HEAD—bypass lookup
and storage. This includes auth, favorites, sync, personalized data, images, game POSTs, and errors (401/403/404/429/5xx).
Cookies, tokens, Vary, debug/internal headers, and any unexpected response header prevent public caching.

The public handler fetches GamerPower without forwarding user context or upstream headers. Image URLs use fixed
`PUBLIC_BASE_URL`. Its public marker attests to safe JSON; it does not automatically detect secrets in the body.
Before adding user context or sensitive fields, remove that marker and disable/purge its edge rule.
Keep new authentication headers aligned with `cacheBypassHeaders` in `ApiCachePolicy.kt` and the expression below.
Browser requests carrying Origin remain uncached; this setup does not eliminate cold starts for game POSTs.
With the origin guard enabled, Cloudflare must attest to the original safe request using the transform below.
This accommodates Cloud Run transport-header rewrites without trusting visitor forwarding headers.

## 1. Prepare the origin

1. Add the domain to Cloudflare and change registrar nameservers, preserving web/email and verification records.
2. Google Cloud Console → Cloud Run → Domain mappings: reuse a mapping or verify ownership and create one.
   Add Google's exact records in Cloudflare → DNS → Records, initially **DNS-only**. A CNAME to `run.app` alone
   is insufficient. Wait for Google's certificate and verify origin HTTPS before enabling the proxy.
3. Set `PUBLIC_BASE_URL=https://api.example.com`, `PUBLIC_CACHE_ENABLED=false`, and exact `CORS_ALLOWED_ORIGINS`.
   Use origins without paths; preserve existing allowed origins. Keep credentialed CORS and wildcards disabled.
   Local browser development also needs its exact localhost origin in this list.

Direct domain mapping is Preview, region-limited, permits TLS 1.0/1.1, and is not recommended by Google for production.
Use it only if those limitations are accepted; adding a load balancer would add billing.
[Google domain mapping](https://docs.cloud.google.com/run/docs/mapping-custom-domains).

## 2. Configure Cloudflare

SSL/TLS → Overview: **Full (strict)**. Set minimum edge TLS **1.2** and redirect HTTP to HTTPS.
Review other apps before zone-wide changes. Keep Always Online and stale-on-error overrides disabled for the API.
Audit Page Rules, Cache Rules, transforms, and Workers routes: remove conflicting API cache overrides and rewrites.
Do not strip bypass headers or add CORS response-header transforms; preserve Ktor's security headers.
Keep WAF/rate limits; avoid browser challenges that break mobile/API clients.

Caching → Cache Rules → Create rule → Custom filter expression → Edit expression.
Create these rules in order after unrelated rules; allow no later rule to override them.

**Rule 1 — API bypass.** Cache eligibility: **Bypass cache**. Keep enabled during rollout and rollback.

```text
http.host eq "api.example.com"
```

**Rule 2 — Public giveaway list.** Create **disabled** until cutover:

```text
(http.host eq "api.example.com"
 and ssl
 and http.request.method eq "GET"
 and raw.http.request.uri eq "/v1/giveaways"
 and http.request.uri eq "/v1/giveaways"
 and not http.request.headers.truncated
 and (not any(lower(http.request.headers.names[*])[*] eq "content-length")
      or (len(http.request.headers["content-length"]) eq 1
          and http.request.headers["content-length"][0] eq "0"))
 and not any(lower(http.request.headers.names[*])[*] in {
   "authorization" "proxy-authorization" "cookie" "origin"
   "x-api-key" "x-auth-token" "x-user-id" "x-session-id"
   "range" "transfer-encoding"
   "x-http-method-override" "x-http-method" "x-method-override"
   "x-forwarded-host" "x-host" "x-original-url" "x-rewrite-url" "forwarded"
 }))
```

- Cache eligibility: **Eligible for cache**.
- Edge TTL: **Use cache-control header if present, bypass cache if not** (`bypass_by_default`).
- Browser TTL: **Respect origin**. Serve stale content while revalidating: **Enabled**.
- Keep Origin Cache Control enabled. No TTL overrides, Status Code TTLs, or **Ignore cache-control header**.
- Keep the default URL-based cache key; do not ignore queries or add cookies, identity, device, or arbitrary headers.

Exact raw/normalized URI matching prevents path aliases and extension tricks from entering the public rule.
Empty/case-varied bypass headers and truncated inspection fail closed; only absent or single `Content-Length: 0`
is allowed. Do not add `s-maxage`, `must-revalidate`, or `proxy-revalidate`: Cloudflare disables stale revalidation
with those directives. Only a previous public 200 may be served during the 30-second revalidation window.
[Cache settings](https://developers.cloudflare.com/cache/how-to/cache-rules/settings/) and
[Origin Cache Control](https://developers.cloudflare.com/cache/concepts/cache-control/).

Tiered Cache (sometimes under Smart Shield): enable Smart Tiered Cache only after verifying it adds no charge
and reviewing other zone origins. Do not enable paid Argo, Cache Reserve, or plan upgrades.

## 3. Cut over safely

1. Switch **all API A/AAAA/CNAME records to Proxied**, TTL Auto, after origin HTTPS and Rule 1 are verified.
2. Update GitHub's `BACKEND_BASE_URL` and local client configuration; rebuild and migrate clients and monitors.
   Enabling the guard or disabling `run.app` blocks older clients still using that URL.
3. Generate a private secret with at least 256 bits of entropy (e.g. 64 random hex characters).
   Rules → Transform Rules → Modify Request Header: match `http.host eq "api.example.com" and ssl`;
   **Set static** `X-Origin-Verify` to the secret and `X-Public-Cache-Eligible` to `0`, overwriting visitor values.
   Immediately after it, add a transform using **exactly Rule 2's full expression**; set static
   `X-Public-Cache-Eligible` to `1`. Both transforms must stay enabled, ordered, and free of later overrides.
   Never strip credential or bypass headers. Configure the secret as Cloud Run's `CLOUDFLARE_ORIGIN_SECRET`.
   Use existing secure storage without adding a paid service.
   Restrict IAM/rule-editor access; keep secrets out of clients, Git, command arguments, logs, and screenshots.
4. Verify proxied success and direct-origin rejection. Cloud Run → service → Networking → Endpoints:
   disable the **Default HTTPS endpoint URL**; remove unnecessary tags/aliases. Preserve ingress `all` for
   domain mapping, the TCP startup probe, existing secret bindings, authentication, rate limiting, and validation.
5. Purge previous API objects, including images, through Caching → Configuration → Purge Cache.
   Use hostname purge if available; otherwise plan a zone purge that accounts for other apps.
   Verify success: single-URL purge can fail with GET-only rules. Then enable Rule 2 and `PUBLIC_CACHE_ENABLED=true`.

The guard rejects missing, forged, and duplicate secrets before upstream calls. It does not provide network
isolation: direct traffic can still cause request/startup costs. Cloudflare IP headers cannot prove origin;
Cloud Run does not enforce Authenticated Origin Pulls. Keep the guard during DNS-only certificate renewal;
monitor renewal because proxy redirects can intercept Google's validation. Rotate both secret values together.
[Default URL controls](https://docs.cloud.google.com/run/docs/securing/ingress#disable_the_default_url).

## 4. Verify and roll back

`ApiCachePolicyTest` covers the origin policy. The edge checker verifies actual Cloudflare behavior with dummy
credentials against a warm public entry, including queries, methods, path tricks, CORS, and leaked headers:

```sh
python3 backend/scripts/check-cloudflare-cache.py https://api.example.com https://your-web-origin.example
curl --silent --show-error --dump-header - --output /dev/null https://api.example.com/v1/giveaways
```

Repeat the GET; HEAD (`curl -I`) deliberately bypasses caching. Expected `CF-Cache-Status`:

- **MISS:** eligible response fetched from origin. **HIT:** cached response served.
- **UPDATING:** stale public response served during revalidation after 60 seconds, for at most 30 more seconds.
  Fast refreshes can make it hard to observe; Tiered Cache can make the first local request a HIT.
- **BYPASS/DYNAMIC:** excluded request. Require `private, no-store`, no `Age`, and no leaked credentials/cookies.
  Cloudflare-generated errors may omit this header.

Test the old `run.app`, tags/aliases, and mapped origin directly using `curl --resolve api.example.com:443:ORIGIN_IP`
with TLS verification and forged Cloudflare/secret headers. Require 403, or Google 404 for a disabled URL.
Check normal browser/game requests, exact CORS origins, no credentialed CORS, and existing security headers.
Use staging to inject 401/403/429/5xx; require no-store and no cache hits. Repeat from another network.
[Cache statuses](https://developers.cloudflare.com/cache/concepts/cache-responses/).

**Rollback:** disable Rule 2, retain Rule 1, set `PUBLIC_CACHE_ENABLED=false`, and purge existing objects.
Keep the proxy and origin guard enabled.

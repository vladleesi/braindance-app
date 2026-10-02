# Cloudflare in front of Cloud Run

Cloudflare DNS/CDN → existing Cloud Run backend. No Workers or new paid services.
Keep minimum instances **0** and preserve CPU, memory, CPU Boost, billing mode, scaling, and existing security controls.
Examples use placeholders; keep actual hostnames, identifiers, and secrets in ignored private configuration.

## Cache policy

Shared caching is disabled by default (`PUBLIC_CACHE_ENABLED=false`); the origin guard is unset until configured.

| Successful anonymous GET | Cache-Control |
| --- | --- |
| `/v1/games/details?id=42` | `public, max-age=3600, stale-while-revalidate=86400` |
| `/v1/games/anticipated?pageSize=20` | `public, max-age=300, stale-while-revalidate=86400` |
| `/v1/games/popular?ids=42,43&pageSize=20` | `public, max-age=300, stale-while-revalidate=86400` |
| `/v1/games/popularity?type=34&pageSize=40` | `public, max-age=300, stale-while-revalidate=86400` |
| `/v1/giveaways` and `/v1/giveaways/{id}` | `public, max-age=300, stale-while-revalidate=3600` |
| `/v1/giveaways/image?url=...` | `public, max-age=86400, stale-while-revalidate=604800` |
| Everything else | `private, no-store` |

Game feeds change slowly; their stale window can hide an origin restart. Game details get a longer freshness
period. Giveaway availability changes more often, so stale offers are limited to one hour; redemption still
happens at the provider. Image URLs identify public assets and can tolerate longer staleness.
Only HTTP 200 responses explicitly marked public by a handler can be stored. Authentication, cookies, identity,
method overrides, suspicious forwarding headers, unknown/duplicate query parameters, errors, health, preflight,
and non-GET methods (including legacy POST and HEAD) remain uncached. New routes default to private.

The API never forwards visitor context or upstream response headers. Rewritten image links use fixed
`PUBLIC_BASE_URL`. Before adding sensitive fields or user context, remove the public marker and disable/purge
its edge rule. Keep authentication headers aligned with `cacheBypassHeaders` in `ApiCachePolicy.kt` and the rule below.
Cloudflare must attest to the original safe request using the guarded transform; this accommodates Cloud Run
transport-header rewrites without trusting visitor forwarding headers.

Browser GETs with a single exact allowed Origin are now eligible too. CORS keeps the existing allowlist and no
credential support. Every handled response varies on Origin, including requests without Origin or from a denied
origin. Denied, malformed and duplicate origins remain no-store. Allow only the exact CORS response headers the
handler emits; cookies, identity, tokens, debugging headers, other Vary values and cache overrides fail closed.
Cloudflare's default cache key includes Origin; preserve it and configure `Vary: Origin` as passthrough where
Cache Rules Vary is configured. Never ignore/normalize Origin or add wildcard CORS transforms.
[Cloudflare CORS](https://developers.cloudflare.com/cache/cache-security/cors/) and
[Vary](https://developers.cloudflare.com/cache/concepts/vary/).

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

**Rule 2 — Public reads.** Create **disabled** until cutover:

```text
(http.host eq "api.example.com"
 and ssl
 and http.request.method eq "GET"
 and raw.http.request.uri.path eq http.request.uri.path
 and (http.request.uri.path in {
   "/v1/giveaways" "/v1/games/details" "/v1/games/anticipated"
   "/v1/games/popular" "/v1/games/popularity"
 } or starts_with(http.request.uri.path, "/v1/giveaways/"))
 and (not any(lower(http.request.headers.names[*])[*] eq "origin")
      or (len(http.request.headers["origin"]) eq 1
          and http.request.headers["origin"][0] in {"https://app.example.com"}))
 and not http.request.headers.truncated
 and (not any(lower(http.request.headers.names[*])[*] eq "content-length")
      or (len(http.request.headers["content-length"]) eq 1
          and http.request.headers["content-length"][0] eq "0"))
 and not any(lower(http.request.headers.names[*])[*] in {
   "authorization" "proxy-authorization" "cookie"
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
- Preserve the default cache key, including the complete query string and Origin. Do not ignore queries/Origin
  or add cookies, identity, device, or arbitrary headers. If Vary settings are present, use Origin passthrough.
- Match the exact origin list to `CORS_ALLOWED_ORIGINS` in both this rule and its attestation transform.

Raw/normalized path matching excludes encoded aliases. The origin separately validates endpoint paths and query
schemas; an unknown path or query cannot obtain a public marker/cache policy. Keep the full query in the cache key.
Empty/case-varied bypass headers and truncated inspection fail closed; only absent or single `Content-Length: 0`
is allowed. Do not add `s-maxage`, `must-revalidate`, or `proxy-revalidate`: Cloudflare disables stale revalidation
with those directives. Only a previous public 200 may be served during that endpoint's stale window.
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
- **UPDATING:** a previous public response served during its stale revalidation window.
  Fast refreshes can make it hard to observe; Tiered Cache can make the first local request a HIT.
- **BYPASS/DYNAMIC:** excluded request. Require `private, no-store`, no `Age`, and no leaked credentials/cookies.
  Cloudflare-generated errors may omit this header.

Test the old `run.app`, tags/aliases, and mapped origin directly using `curl --resolve api.example.com:443:ORIGIN_IP`
with TLS verification and forged Cloudflare/secret headers. Require 403, or Google 404 for a disabled URL.
Check each GET in the policy table twice, with and without an allowed Origin. Require HIT on a warm entry,
correct allow-origin, and `Vary: Origin`. Alternate two allowed origins, an unrelated origin and no Origin to check
separation. CORS header changes require purging every cached Origin variant; a purge of only the bare URL can
leave variants behind. Follow Cloudflare's CORS purge instructions or plan a full purge accounting for other apps.
Check legacy POST compatibility, exact CORS origins, no credentialed CORS, and existing security headers.
Use staging to inject 401/403/429/5xx; require no-store and no cache hits. Repeat from another network.
[Cache statuses](https://developers.cloudflare.com/cache/concepts/cache-responses/).

**Rollback:** disable Rule 2, retain Rule 1, set `PUBLIC_CACHE_ENABLED=false`, and purge existing objects.
Keep the proxy and origin guard enabled.

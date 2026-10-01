# Cloudflare in front of Cloud Run

Cloudflare DNS/CDN → existing Cloud Run service → existing IGDB/GamerPower integrations. No Workers.
This is an opt-in rollout: the application defaults to no shared caching and an unset origin guard.
Replace `api.example.com` with your API hostname throughout. No dashboard or Cloud Run changes are automated here.
Keep Cloud Run minimum instances **0** and preserve CPU, memory, CPU Boost, billing mode, scaling limits,
IAM, upstream rate limiting, timeouts, and validation. Do not provision another paid Google Cloud service.

## Public response contract

| Request/response | Cache policy |
| --- | --- |
| Exact anonymous `GET /v1/giveaways`, no query, no Origin, successful public JSON, configured HTTPS host | Opt-in public, fresh 60 seconds, stale revalidation up to 30 seconds |
| Any Authorization, Cookie, identity/token header, Origin, Range, request body framing or override header below | Bypass cache lookup AND storage; `private, no-store` at origin |
| Giveaway details, images, health, game POSTs, auth, favorites, sync, personalized routes, unknown routes | Bypass; `private, no-store` |
| Any non-GET, including HEAD; any query, including duplicate/unknown/token parameters | Bypass; `private, no-store` |
| Non-200, auth failure, 404, 429, malformed upstream data, exceptions, unavailable credentials | `private, no-store` |
| Set-Cookie, Authorization, Vary, identity/debug/internal headers, or other unexpected origin response headers | `private, no-store` |

The single public handler fetches a public GamerPower list without forwarding client credentials, cookies,
headers, query parameters, or bodies. Image links use fixed `PUBLIC_BASE_URL`, never a forwarded host.
It explicitly marks its body as public; all other handlers are private by default. Upstream response headers
are not relayed. The final Ktor response hook checks both application and content headers before permitting storage.
It does not guess whether arbitrary JSON contains secrets: if this handler ever consumes user context or returns
sensitive fields, remove its public marker and disable/purge its edge rule **before deploying that change**.
New authentication headers must also be added to the bypass list in Kotlin and Cloudflare before use.

Browser requests carrying Origin bypass caching. Existing exact CORS origins, loopback HTTP development origins,
GET/POST/OPTIONS methods, and Content-Type preflight remain supported, without credentialed CORS or wildcards.
The API returns nosniff, no-referrer, frame protection, and `default-src 'none'; frame-ancestors 'none'`.
Do not add CORS response-header transforms or reflect arbitrary request headers. CORS is not access control.

## 1. DNS, TLS, and origin protection

1. Add the domain to Cloudflare and use its nameservers. Preserve existing records and restrict administrator access.
2. Reuse the existing Cloud Run custom-domain route. Otherwise, Google Cloud Console → Cloud Run → Domain mappings:
   verify the domain and obtain Google's exact DNS records. Add those values in Cloudflare → DNS → Records.
   Keep verification TXT records DNS-only. Keep API records DNS-only until Google's certificate is ready, then switch
   **all API A/AAAA/CNAME records to Proxied**, TTL Auto. A CNAME to `run.app` alone does not establish the mapping.
3. Cloudflare → SSL/TLS → Overview: **Full (strict)**. Use minimum edge TLS 1.2 and redirect HTTP to HTTPS.
   Scope changes to the API where possible; review other apps before zone-wide changes. Enable API HSTS only after
   HTTPS works; avoid includeSubDomains/preload without reviewing the entire domain.
4. Deploy with `PUBLIC_CACHE_ENABLED=false`, `PUBLIC_BASE_URL=https://api.example.com`, and exact
   `CORS_ALLOWED_ORIGINS`. Update GitHub's `BACKEND_BASE_URL` and local client build configuration and rebuild clients.
   Installed clients still using `run.app` must migrate before the guard/default-URL restriction is enabled.
5. Generate a private random secret with at least 256 bits of entropy, such as 64 random hex characters. Bind a pinned
   secret version as Cloud Run's `CLOUDFLARE_ORIGIN_SECRET`. Cloudflare → Rules → Transform Rules → Modify Request Header:
   match `http.host eq "api.example.com" and ssl`; **Set static** `X-Origin-Verify` to that secret, overwriting visitor
   values. Do not append or strip credentials, cookies, Origin, or bypass headers. Prevent later guard overrides.
   Keep secrets out of Git, clients, command arguments, logs, and screenshots; restrict access to rule editors.
6. Confirm proxied requests succeed and direct requests fail. Move HTTP monitors to the proxied domain and retain
   the existing TCP startup probe. After client migration, Cloud Run → service → Networking → Endpoints: disable
   the Default HTTPS endpoint URL. Remove unnecessary traffic tags/domain aliases. Preserve ingress `all` for this
   mapping route; internal/load-balancer-only ingress cannot admit it. Preserve all cost settings and secret bindings.

**Origin limitation:** direct Cloud Run domain mapping is Preview, region-limited, permits older origin TLS,
and is not Google's recommended production ingress. If no acceptable custom-domain route exists, resolve that
before rollout. A Google load balancer would add billing and is outside this change.
[Google domain mapping](https://docs.cloud.google.com/run/docs/mapping-custom-domains) and
[default URL controls](https://docs.cloud.google.com/run/docs/securing/ingress#disable_the_default_url).

The guard rejects missing, forged, or duplicate secrets before upstream calls. It is application protection;
direct traffic can still reach Google's infrastructure and cause request/startup costs. Cloudflare IP headers
are never proof of origin. Cloud Run does not enforce Cloudflare Authenticated Origin Pulls client certificates.
Rotate the Cloud Run binding and Cloudflare value together if exposed. Keep the guard enabled during DNS-only renewal.

## 2. Cache Rules

Cloudflare → Caching → Cache Rules → Create rule → Custom filter expression → Edit expression.
Audit existing Page Rules, Cache Rules, response transforms, and Workers routes for this hostname first.
No Worker should handle this API. Remove conflicting broad Cache Everything/Edge TTL overrides for the API.

**Rule 1 — API default bypass**, placed after existing unrelated rules:

```text
http.host eq "api.example.com"
```

Cache eligibility: **Bypass cache**. This also prevents extension-based caching/web cache deception on errors,
images, or sensitive paths. Leave it active during rollout and rollback.

**Rule 2 — Public giveaway list**, placed immediately after Rule 1, with no later API caching rule:

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
- Browser TTL: **Respect origin**. Do not set a minimum/override browser TTL for this API.
- Serve stale content while revalidating: **Enabled**.
- Do not configure Status Code TTLs or **Ignore cache-control header**. These can override `no-store` and
  strip Set-Cookie while caching. Keep Origin Cache Control enabled (default on Free/Pro/Business).
- Cache key: keep the **default**, full scheme/host/path/query; do not ignore queries or add device,
  language, country, cookies, client IP, secret, or arbitrary request headers. Do not rewrite this path/host.
  All queries bypass, including otherwise ignored tracking parameters. CORS and override headers bypass
  before lookup, so authenticated/browser responses cannot reuse the public entry.
- Enable Cache Deception Armor if available without a plan change; exact raw/normalized URI matching and
  default bypass remain mandatory regardless. Do not normalize sensitive aliases into this cacheable URL.

Presence checks include empty/case-varied headers; truncated inspection fails closed. Keep the list aligned
with `cacheBypassHeaders` in `ApiCachePolicy.kt`. Only absent or single canonical `Content-Length: 0` is allowed.
See [Cache Rule settings](https://developers.cloudflare.com/cache/how-to/cache-rules/settings/).

Purge previous API entries after installing rules, including old public image responses; use a hostname purge
if available, otherwise a zone purge during a planned window. Single-URL purge may not match GET-only rules;
verify the purge rather than assuming success. Then set `PUBLIC_CACHE_ENABLED=true` on Cloud Run only after
checking TLS, origin guard, rules, and payloads. It requires an HTTPS `PUBLIC_BASE_URL`.
The public header is `public, max-age=60, stale-while-revalidate=30, stale-if-error=0`.
Do not add `s-maxage`, `must-revalidate`, or `proxy-revalidate`: they prevent stale revalidation in Cloudflare's
current implementation. Disable Always Online for this API; do not enable stale-on-error overrides.
[Origin Cache Control](https://developers.cloudflare.com/cache/concepts/cache-control/) explains these interactions.
Only a previous public 200 may be served within the explicit 30-second revalidation window; errors themselves
are never stored, and there is no additional stale-if-error window.

## 3. Tiered Cache and abuse controls

Cloudflare → Caching → Tiered Cache (sometimes under Smart Shield): enable **Smart Tiered Cache** only if included
without extra charges. Review other zone origins first; use the existing origin's actual GCP region hint if available.
It consolidates public misses without changing cache eligibility. Do not enable paid Cache Reserve, Argo, plan upgrades,
Google Cloud CDN, or another paid service.

Preserve WAF, edge rate limiting, backend IGDB limiting, request validation, and authentication. Avoid browser
challenge pages that break API/mobile clients. Do not log bodies, credentials, query tokens, the origin secret,
or internal diagnostics. Retain the restrictive CORS and security headers described above.

## 4. Verification and rollback

`ApiCachePolicyTest` checks the origin allowlist, credentials/identity headers, queries/methods, errors,
unexpected response headers, CORS, and forged origin secrets. It cannot validate your Cloudflare dashboard rules.
After deployment, optionally run the edge checker with your configured allowed web origin:

```sh
python3 backend/scripts/check-cloudflare-cache.py https://api.example.com https://your-web-origin.example
```

It warms a public entry, repeats excluded requests against the same URL with dummy credentials, and checks no-store,
cache bypass, CORS, and path/method variants. It prints no bodies or secrets. Repeat from another network after changes.

For manual inspection use GET, not `curl -I` (HEAD deliberately bypasses):

```sh
curl --silent --show-error --dump-header - --output /dev/null https://api.example.com/v1/giveaways
```

Repeat twice. **MISS** means an eligible object was fetched; **HIT** means cached data was delivered.
With Tiered Cache, the first local response may already be HIT. After 60 seconds, concurrent GETs within the
next 30 seconds may show **UPDATING** while the prior public response is refreshed; a fast refresh can make it
hard to observe. **BYPASS** or **DYNAMIC** is expected for excluded requests; absence of CF-Cache-Status can
occur for Cloudflare-generated errors. `Age` usually appears on hits; it is not a security boundary.
See [Cloudflare cache response statuses](https://developers.cloudflare.com/cache/concepts/cache-responses/).

Test old `run.app`, tagged/alias URLs, and the mapping directly with forged Cloudflare/IP/origin-secret headers.
Require 403 before upstream calls, or Google 404 for a disabled URL. To test the mapped origin directly, use Google's
origin IP in `curl --resolve api.example.com:443:ORIGIN_IP`; retain TLS verification. Check normal browser/game POST
requests too. Use staging for injected 401/403/429/5xx failures; require no-store and no cache hits. Future user routes
must remain outside the allowlist and be checked with two accounts against a warm anonymous entry.

Before cutover, confirm no broad rule/TTL override, header-stripping transform, host rewrite, wildcard CORS, or
unguarded origin alias defeats these checks. Keep min instances 0. To stop caching: disable Rule 2, retain Rule 1,
set `PUBLIC_CACHE_ENABLED=false`, and **purge existing objects**. Keep the proxy and origin guard enabled.

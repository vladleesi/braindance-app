#!/usr/bin/env python3
"""Check the configured edge with dummy credentials; never prints response bodies or secret headers."""

import sys
import time
import urllib.error
import urllib.parse
import urllib.request


def require(condition, message):
    if not condition:
        raise RuntimeError(message)


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def main():
    require(len(sys.argv) == 3, "Usage: check-cloudflare-cache.py HTTPS_API_ORIGIN HTTPS_WEB_ORIGIN")
    base, web_origin = sys.argv[1:]
    for value in (base, web_origin):
        parsed = urllib.parse.urlsplit(value)
        require(
            parsed.scheme == "https" and parsed.hostname and not parsed.username
            and not parsed.password and not parsed.path and not parsed.query and not parsed.fragment,
            "Arguments must be exact HTTPS origins without a trailing slash",
        )
    opener = urllib.request.build_opener(NoRedirect())

    def request(path, method="GET", headers=None):
        req = urllib.request.Request(base + path, headers=headers or {}, method=method)
        try:
            response = opener.open(req, timeout=40)
        except urllib.error.HTTPError as error:
            response = error
        with response:
            status = response.code
            result = response.headers
            # Drain the bounded public response for connection completion; do not log/store its body.
            require(len(response.read(3 * 1024 * 1024 + 1)) <= 3 * 1024 * 1024, "Unexpected response size")
        require(result.get("CF-Ray"), "Response did not pass through Cloudflare")
        cache_status = result.get("CF-Cache-Status", "").upper()
        print(f"{method} {path.split('?')[0]}: HTTP {status}, CF-Cache-Status={cache_status or 'absent'}")
        return status, result, cache_status

    saw_hit = False
    for _ in range(5):
        status, headers, cache_status = request("/v1/giveaways")
        require(status == 200, "Public list unavailable; verify origin secret, upstream, TLS and WAF")
        require("public" in headers.get("Cache-Control", ""), "Origin public caching is not enabled")
        require(not headers.get("Set-Cookie"), "Public response contains a cookie; inspect before accepting cache")
        if cache_status == "HIT":
            saw_hit = True
            break
        time.sleep(1)
    require(saw_hit, "No warm HIT observed; check rules, Origin Cache Control, Development Mode and Tiered Cache")

    cases = [
        ("/v1/giveaways", "GET", {"Authorization": "Bearer dummy-cdn-check"}),
        ("/v1/giveaways", "GET", {"aUtHoRiZaTiOn": ""}),
        ("/v1/giveaways", "GET", {"Cookie": "session=dummy-cdn-check"}),
        ("/v1/giveaways", "GET", {"Cookie": ""}),
        ("/v1/giveaways", "GET", {"X-User-Id": "dummy-user"}),
        ("/v1/giveaways", "GET", {"X-Api-Key": "dummy-key"}),
        ("/v1/giveaways", "GET", {"X-Auth-Token": "dummy-token"}),
        ("/v1/giveaways", "GET", {"X-Session-Id": "dummy-session"}),
        ("/v1/giveaways", "GET", {"Proxy-Authorization": "dummy"}),
        ("/v1/giveaways", "GET", {"X-Forwarded-Host": "evil.example"}),
        ("/v1/giveaways", "GET", {"X-Original-URL": "/v1/favorites"}),
        ("/v1/giveaways", "GET", {"X-HTTP-Method-Override": "POST"}),
        ("/v1/giveaways", "GET", {"Origin": web_origin}),
        ("/v1/giveaways", "GET", {"Origin": "https://evil.example"}),
        ("/v1/giveaways?token=dummy", "GET", {}),
        ("/v1/giveaways?a=1&a=2", "GET", {}),
        ("/v1/giveaways/42", "GET", {}),
        ("/v1/giveaways/image?url=https%3A%2F%2Fevil.example%2Foffers%2Fa.png", "GET", {}),
        ("/v1/giveaways/file.css", "GET", {}),
        ("/v1/favorites/file.css", "GET", {}),
        ("/health", "GET", {}),
    ]
    cases.extend(("/v1/giveaways", method, {}) for method in ("POST", "PUT", "PATCH", "DELETE", "HEAD"))
    for path, method, request_headers in cases:
        for _ in range(2):
            status, headers, cache_status = request(path, method, request_headers)
            directives = {item.strip().lower() for item in headers.get("Cache-Control", "").split(",")}
            require({"private", "no-store"} <= directives, "Excluded response lost private/no-store directives")
            require(cache_status in ("BYPASS", "DYNAMIC"), "Excluded request entered the shared-cache path")
            require(headers.get("Age") is None, "Excluded request has cached Age")
            require(headers.get("X-Origin-Verify") is None, "Origin verification header leaked")
            require(headers.get("Authorization") is None, "Authorization response header leaked")
            origin = request_headers.get("Origin")
            if origin:
                expected = web_origin if origin == web_origin else None
                require(headers.get("Access-Control-Allow-Origin") == expected, "CORS origin mismatch")
                require(headers.get("Access-Control-Allow-Credentials") is None, "Credentialed CORS is enabled")

    status, headers, cache_status = request("/v1/giveaways")
    require(status == 200 and cache_status in ("HIT", "UPDATING"), "Public entry changed after excluded probes")
    require(headers.get("Access-Control-Allow-Origin") is None, "Browser headers contaminated public entry")
    print("PASS: warm public entry and repeated excluded requests stayed separated at this edge.")


if __name__ == "__main__":
    try:
        main()
    except (RuntimeError, urllib.error.URLError, TimeoutError) as error:
        # Do not print the exception: it can contain a request URL or provider diagnostic information.
        print("FAIL: edge regression check failed; inspect the last request and dashboard settings.", file=sys.stderr)
        if isinstance(error, RuntimeError):
            print(str(error), file=sys.stderr)
        sys.exit(1)

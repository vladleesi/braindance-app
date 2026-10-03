# Braindance API

The Kotlin/JVM Ktor service proxies IGDB and GamerPower without exposing Twitch credentials to clients. Its
independent version is `0.5.1` in `build.gradle.kts`.
The single-instance setup uses an in-memory IGDB limiter and needs no Redis service.

## Configuration

| Variable | Purpose |
| --- | --- |
| `TWITCH_CLIENT_ID` | Twitch developer application client ID for IGDB |
| `TWITCH_CLIENT_SECRET` | Twitch developer application secret for IGDB |
| `IGDB_RATE_LIMITER` | Set `in-memory` for one instance; `redis` is the default if omitted |
| `REDIS_URL` | Shared Redis connection URL, required only in Redis mode |
| `CORS_ALLOWED_ORIGINS` | Comma-separated exact web origins, including local HTTP origins when needed |
| `PUBLIC_BASE_URL` | External API origin used in image proxy URLs, required behind a TLS reverse proxy |
| `PUBLIC_CACHE_ENABLED` | Default `false`; `true` enables the public GET policy in `cloudflare.md` |
| `CLOUDFLARE_ORIGIN_SECRET` | Optional origin guard; private value with at least 32 non-space ASCII characters |
| `PORT` | Listening port, default `8080` |

Create a confidential application in the [Twitch Developer Console](https://dev.twitch.tv/console/apps). IGDB uses
its client ID and secret; GamerPower requires no key. Supply credentials through your hosting provider's secret
store or local environment. Do not put them in Git, `local.properties`, mobile/browser builds, or container images.
See [`example.env`](example.env) for local variable names and replace every placeholder privately.
`/healthz` returns 503 until both Twitch values are present. The backend URL itself is public configuration: set
`BACKEND_BASE_URL` in the ignored root `local.properties` or `ORG_GRADLE_PROJECT_BACKEND_BASE_URL` for client builds.

## Run and package

Use JDK 21. For a single instance, set `IGDB_RATE_LIMITER=in-memory`; no Redis is needed.
This mode permits four requests per rolling second per process; overlapping instances can exceed the global limit.
See [optional shared rate limiting](#optional-shared-rate-limiting) before increasing the instance count. Start locally with
`./gradlew :backend:run` from the repository root, then request `GET http://localhost:8080/healthz`.
The application reads exported environment variables; it does not load env files automatically. For local use,
copy `backend/example.env` to the ignored `backend/.env`, edit it, then run
`set -a; . backend/.env; set +a` before starting Gradle. Set `PUBLIC_BASE_URL=http://localhost:8080` locally.
Docker can load the same file with `docker run --env-file backend/.env -p 8080:8080 braindance-api:0.5.1`.

Run `./gradlew :backend:test :backend:installDist` to test and create a distribution. Build a container with
`docker build -f backend/Dockerfile -t braindance-api:0.5.1 backend`. Inject environment variables at runtime and
publish port 8080 through the hosting provider's HTTPS proxy. GitHub Actions validates the distribution and builds
the image. After all checks pass, backend-impacting changes pushed or promoted to `master` publish version and
commit tagged images to GitHub Container Registry and deploy the published image digest to the existing Cloud Run
service using keyless authentication. Publication builds the exact validated commit.
Manual runs of **Publish and Deploy Backend** on `master` force deployment of the current `master` backend.
See [Cloud Run deployment](cloud-run.md) for a conservative configuration, secret handling, and verification.

The Docker build trains a JVM class-data archive using placeholder credentials and only the local health route.
The image loads this archive on startup to reduce repeated JVM class-loading work. It uses the same JRE and JAR
paths for training and runtime; no real secrets or external API calls are involved. No Cloud Run settings change.
`JAVA_OPTS` defaults to `-Xshare:auto -XX:SharedArchiveFile=/app/startup.jsa`; overriding `JAVA_OPTS` disables this
default, and Java can fall back to normal class loading if the archive is incompatible.

Twitch tokens are cached per process, refreshed before expiry, and refreshed once on an IGDB 401.
The API is public; apply host level abuse controls as needed.

## Hosting elsewhere or from a fork

The backend has no Google Cloud runtime dependency. Run the container on your chosen host or run the packaged
distribution with Java 21. The server binds `0.0.0.0` and reads `PORT`, defaulting to `8080`.
Supply your own Twitch credentials and the environment variables above. Set `PUBLIC_BASE_URL` to the external
HTTPS API origin, `CORS_ALLOWED_ORIGINS` to your website origins, and the clients' `BACKEND_BASE_URL` before building.
For one instance, explicitly set `IGDB_RATE_LIMITER=in-memory`; multiple instances require shared Redis rate limiting.

The `publish` job in [the backend workflow](../.github/workflows/publish-backend.yml) builds and publishes to your
repository's GHCR package. Replace or remove its `deploy` job to deploy elsewhere while keeping image publication.
That job targets Cloud Run for validated backend-impacting changes or manual runs on `master`, and fails if the
required `GCP_*` repository secrets are missing; it does not automatically skip deployment for an unconfigured fork.

## API

| Endpoint | Request |
| --- | --- |
| `GET /healthz` | Health and credential availability |
| `GET /health` | Application ready; no credentials, client creation or external calls |
| `GET /v1/games/details?id=42` | Game details |
| `GET /v1/games/anticipated?pageSize=20` | Upcoming games; optional `currentTimestamp` overrides server time |
| `GET /v1/games/popular?ids=42,43&pageSize=20` | Up to 50 comma-separated game IDs |
| `GET /v1/games/popularity?type=34&pageSize=40` | Popularity primitives |
| `GET /v1/giveaways` | Giveaway list |
| `GET /v1/giveaways/42` | Giveaway details |
| `GET /v1/giveaways/image?url=...` | Images under `https://www.gamerpower.com/offers/` |

Legacy game POSTs retain their existing JSON bodies and behavior for installed clients and remain no-store.
GET parameters reject unknown/duplicate keys and retain the same integer bounds. Game URLs and POST bodies
are capped at 2 KiB. IGDB JSON is capped at 1 MiB, GamerPower JSON at 2 MiB, and images at 5 MiB.
Token requests time out after 5 seconds; other upstream requests after 8 seconds. Responses default to
`private, no-store`. Explicit anonymous GET successes can opt into the 20-minute policy in
[the Cloudflare guide](cloudflare.md#cache-policy), including browser requests from exact allowed origins.
Health, preflight, errors, legacy POST, credentials and unknown response headers remain no-store.
All responses have `nosniff`, `no-referrer`, frame protection and a restrictive API content security policy.
Browser preflight is supported for `/v1/` and `/health` routes.
Set `CORS_ALLOWED_ORIGINS` to exact production site origins, without paths or trailing slashes.
Keep this allowlist free of wildcards; credentials are not enabled. Loopback HTTP origins remain supported.

See [Cloudflare DNS/CDN setup and security verification](cloudflare.md) before enabling public caching or the
origin guard. Cloud Run remains the origin; no Worker or additional Google Cloud service is required.

[IGDB API](https://api-docs.igdb.com/) · [Twitch application registration](https://dev.twitch.tv/docs/authentication/register-app/)

## Cold-start timing

Clients launch one best-effort `/health` request from shared Koin startup without awaiting it or retrying.
It can overlap UI startup with instance activation; it cannot guarantee a warm instance before the first data request.
There is no periodic traffic, and minimum instances remain zero.

Startup logs report JVM uptime at entry to `main`, environment/config validation, optional Redis pool creation,
service creation, server creation, application route installation, and server readiness. HTTP client initialization
is timed separately on its first use; it is already lazy and shared. A bounded first-completion log per route
reports request time, and the first token/IGDB/giveaway/image operation reports upstream time without URLs,
headers, credentials or payloads. No application database or runtime Secret Manager client exists; optional Redis
network work happens on the IGDB limiter path. Container provisioning and platform secret injection occur before
these application measurements and need Cloud Run request/startup logs for attribution.

To compare a real cold request with a warm request, let the service naturally scale to zero, then time a no-store
health GET and a game GET (or use staging with public caching disabled). Do not change scaling or add keep-alives.
Edge HIT/UPDATING timing measures CDN delivery rather than origin startup. Inspect startup and first-operation logs
from the same instance to separate JVM/application readiness, HTTP initialization and upstream latency.

## Optional shared rate limiting

For multiple instances, set `IGDB_RATE_LIMITER=redis` and point every replica at the same `REDIS_URL` database and
keyspace. The atomic sliding-window script allows four IGDB calls per rolling second across replicas and fails
closed if Redis is unavailable. Keep Redis private, use TLS/authentication, `noeviction`, and durable persistence.
After state loss, pause IGDB traffic for at least one second; never use independent primaries during failover.
Store connection credentials in the runtime secret store. This infrastructure is unnecessary for the in-memory setup.

The optional Redis integration test uses `TEST_REDIS_URL` pointing to a disposable Redis database. CI supplies it.

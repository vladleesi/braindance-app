# Cloud Run deployment

This guide contains reusable configuration only. Keep project IDs, service identities, image URLs, runtime origins,
secret references, and generated service URLs in environment variables or the ignored root `.deployment/` directory.
Do not commit service exports, credentials, or populated environment files. CI publishes images and updates the
existing Cloud Run service through Workload Identity Federation. Use interactive authentication locally and
keyless federation in CI, not service-account JSON keys.

## Prerequisites and rate limiting

Install and authenticate the Google Cloud CLI, select your project, and confirm billing is enabled privately.
Use `gcloud config get-value project` locally to confirm the target. The deploying identity needs permission to
deploy, use the runtime identity, and configure the required resources; the runtime identity does not need these roles.

Set `IGDB_RATE_LIMITER=in-memory` for the initial single-instance deployment. It enforces four requests in any
rolling second within a process using a monotonic clock and serialized reservations. No Redis is provisioned.
Cloud Run can temporarily exceed its maximum during instance replacement; overlapping processes may briefly exceed
four requests per second globally. This is an explicit availability/cost tradeoff, not a distributed guarantee.
Before increasing instance count, configure `IGDB_RATE_LIMITER=redis` on every active revision with the same shared
Redis database. Test concurrent reservations and preserve state through failover as described in the
[backend guide](README.md#optional-shared-rate-limiting). Avoid traffic splits and tagged revisions serving public traffic.

## Resources and secrets

Use the active project's region setting when present; otherwise use `us-central1`. Create or reuse one Artifact
Registry Docker repository and a dedicated runtime service account with no project-level roles. Enable only
`run.googleapis.com`, `artifactregistry.googleapis.com`, and `secretmanager.googleapis.com`.
Enable `iam.googleapis.com` when creating service accounts, `cloudbuild.googleapis.com` only for remote container
builds, and `billingbudgets.googleapis.com` only when configuring budget alerts through the CLI.

Create Secret Manager secrets for `TWITCH_CLIENT_ID` and `TWITCH_CLIENT_SECRET`, populate them privately through
the console or protected input files, and grant `roles/secretmanager.secretAccessor` to the runtime service account
on those individual secrets. Never put secret values in CLI arguments, shell history, build arguments, images,
or runtime YAML. Pin numeric secret versions at deployment.
Do not grant the runtime identity build, registry write, IAM administration, or billing access.

Build with JDK 21 using `./gradlew :backend:build :backend:installDist`. Build and push the existing Dockerfile to
Artifact Registry for `linux/amd64`; on an ARM developer machine use Docker Buildx with `--platform linux/amd64`.
If using Cloud Build, upload a temporary directory containing only `Dockerfile`, `docker/prepare-startup-archive.sh`,
and `build/install/backend/`.
Do not upload the repository or ignored developer configuration. Use an image digest for deployment.
Set an Artifact Registry cleanup policy to retain a small rollback history rather than accumulating images.

## Runtime configuration

Create `.deployment/runtime.yaml` privately containing non-secret values only:

```yaml
IGDB_RATE_LIMITER: "in-memory"
CORS_ALLOWED_ORIGINS: "https://app.example.com"
PUBLIC_BASE_URL: "https://api.example.com"
JAVA_TOOL_OPTIONS: "-XX:MaxRAMPercentage=50.0"
```

Replace the example origins locally. Production CORS must be an exact origin without a trailing slash. HTTP
`localhost` and `127.0.0.1` with arbitrary ports are already allowed; no wildcard is needed. `PUBLIC_BASE_URL` must
be the external HTTPS API origin so proxied image links remain HTTPS behind the Cloud Run TLS proxy.
For the initial deployment, omit `PUBLIC_BASE_URL`, keep the service authenticated, obtain its URL, and update the
runtime file and service before granting public access. Cloud Run supplies `PORT`; the server binds `0.0.0.0`.

Export the following deployment-local variables: `GCP_SERVICE_NAME`, `GCP_REGION`, `GCP_RUNTIME_SERVICE_ACCOUNT`,
`IMAGE_DIGEST`, and `SECRET_BINDINGS`. `SECRET_BINDINGS` contains references only, for example
`TWITCH_CLIENT_ID=client-id:1,TWITCH_CLIENT_SECRET=client-secret:1`.
Do not use literal secret values. After resolving the prerequisites:

```sh
gcloud run deploy "$GCP_SERVICE_NAME" \
  --region "$GCP_REGION" \
  --image "$IMAGE_DIGEST" \
  --service-account "$GCP_RUNTIME_SERVICE_ACCOUNT" \
  --min 0 --max 1 --min-instances 0 --max-instances 1 \
  --cpu 1 --memory 512Mi --concurrency 20 --timeout 30s \
  --cpu-throttling --no-cpu-boost --no-session-affinity \
  --port 8080 --ingress all --no-allow-unauthenticated \
  --env-vars-file .deployment/runtime.yaml \
  --set-secrets "$SECRET_BINDINGS"
```

Use a fresh dedicated service without additional containers, GPUs, VPC connectors, volumes, or background jobs.
For an existing service, inspect its settings first: omitted deployment flags do not remove old resources.
After setting `PUBLIC_BASE_URL` and verifying authenticated requests, grant `roles/run.invoker` to `allUsers`
on this service only. The client API is public. CORS restricts browsers, not direct callers or traffic abuse.

## Billing and verification

Before opening public access, configure a small project budget (for example USD 5/month) with actual-spend
alerts at 50%, 90%, and 100% and a forecast alert. Keep budget and billing identifiers outside Git. Alerts-only
budgets do not stop spending. Where available, evaluate a Cloud Run spend-cap budget separately and verify its
coverage and enforcement delay; registry storage, builds, logging, and networking may need separate monitoring.
Instance limits reduce exposure but are not a monetary cap. Under abuse, revoke public invocation and investigate.
Do not add a load balancer, Redis deployment, or other paid service just to follow this guide.

Inspect the deployed service to verify CPU, memory, concurrency, timeout, request-based CPU allocation, runtime
identity, pinned secret references, and both service/revision instance limits. Confirm logs contain no secret values
or upstream bodies without copying sensitive logs into Git or shared reports. Perform only a few smoke requests:

- `/health`: 200 (credential presence only; this does not prove Twitch authentication).
  `/healthz` is preserved in the application, but the Google edge intercepts that path with a 404 on `run.app`.
- `POST /v1/games/details` with JSON `{"id":42}`: successful IGDB response.
- `/v1/giveaways`: successful JSON with HTTPS image proxy URLs; request one returned image URL.
- Invalid game ID: 400; invalid image URL: 400; missing JSON content type: 415.
- Preflight from the configured production origin and both local hosts: 204 and exact allow-origin header.
- Preflight from an unrelated origin: 403 without an allow-origin header.

Set the resulting service URL as the GitHub Actions repository secret `BACKEND_BASE_URL`, then run the existing
develop validation and promotion workflow to publish the web app. Local client builds use the ignored
`local.properties` `BACKEND_BASE_URL` value or
`ORG_GRADLE_PROJECT_BACKEND_BASE_URL`. Verify a real browser request from the published frontend, including an
IGDB view and giveaway image. A successful curl request alone does not verify the published frontend configuration.

## Updating a deployment

After all validation checks pass, pushes to `master` and validated promotions publish and deploy the exact validated
commit when `backend/**` or its root Gradle/wrapper/version-catalog and lint inputs change. Promotions compare against
`master` before promotion; direct pushes compare against `master` before the push. Backend version changes are not a
deployment gate. In GitHub Actions, select **Publish and Deploy Backend → Run workflow** and choose the `master`
branch to force building, testing, publishing, and deploying the current `master` backend.
The GHCR package must be public. The workflow updates only the image of the existing service; it does not create
a service or change runtime settings, secrets, IAM access, or instance limits. Outdated publications are skipped.

Configure repository Actions variables `GCP_PROJECT_ID`, `GCP_REGION`, `GCP_CLOUD_RUN_SERVICE`,
`GCP_WORKLOAD_IDENTITY_PROVIDER`, and `GCP_DEPLOY_SERVICE_ACCOUNT`. The deployment identity needs Cloud Run Developer
on the existing service and Service Account User on its runtime identity. Restrict Workload Identity Federation
to this repository's numeric IDs and the backend publication workflow on `master` and validated `develop` promotions.

The provider's workflow-path conditions must use the current repository name. After a repository rename, update
both `job_workflow_ref` (reusable workflow calls) and `workflow_ref` (direct manual runs) from the previous
`OWNER/REPOSITORY/.github/workflows/publish-backend.yml@refs/heads/BRANCH` paths to the new name. Preserve the
numeric repository/owner ID checks, exact workflow file, allowed branches, issuer, audience, attribute mappings,
and service-account bindings. A checked-out `master` commit does not change the caller's OIDC branch claim:
validated promotions still run with `refs/heads/develop`. Use `job_workflow_ref` to identify the called backend
workflow; `workflow_ref` identifies its caller during reusable runs. Never remove the attribute condition to
resolve `unauthorized_client`. See [GitHub's reusable workflow claims](https://docs.github.com/en/actions/how-tos/secure-your-work/security-harden-deployments/oidc-with-reusable-workflows).

Adding a Secret Manager version still requires updating the service's pinned secret references separately.
Recheck health, an IGDB request, giveaways, and an image after deployment or a secret update.
An unchanged service URL requires no frontend rebuild; changing `BACKEND_BASE_URL` requires a new client build.

## Cloudflare DNS/CDN

Follow [the Cloudflare guide](cloudflare.md) for the exact DNS, TLS, origin-guard, cache-rule, and verification steps.
It preserves this service and all cost settings, including minimum instances of zero. Domain mapping is required
for the custom hostname; a CNAME pointing directly at `run.app` alone is insufficient. Do not enable the origin
guard until clients and health monitors have migrated to the proxied hostname. Keep the guard unset locally.

[Instance limits](https://docs.cloud.google.com/run/docs/configuring/max-instances-limits) ·
[Secret integration](https://docs.cloud.google.com/run/docs/configuring/services/secrets) ·
[Deployment flags](https://docs.cloud.google.com/sdk/gcloud/reference/run/deploy) ·
[Billing alerts](https://docs.cloud.google.com/billing/docs/how-to/budgets)

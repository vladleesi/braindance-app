# Braindance API Worker

The JavaScript backend handles Twitch client credentials and proxies IGDB and GamerPower requests. The mobile app
uses fixed backend endpoints and never receives an OAuth token or the Twitch client secret.

The Worker uses an independent Semantic Version from `package.json`. Each deployment records that version, its
GitHub source commit, and the triggering GitHub actor in Cloudflare Version History.

The request validation, IGDB queries, token lifecycle, response limits, and upstream error handling live in
`src/igdb-backend.mjs`. GamerPower routing and proxy behavior live in `src/gamerpower-backend.mjs`, while shared
HTTP response utilities live in `src/http.mjs`. The modules use standard Web APIs and receive external dependencies
such as `fetch` and logging. `worker.mjs` is the Cloudflare adapter: it supplies Worker secrets and a Durable Object
backed global IGDB rate limiter. A future Node.js adapter can reuse these modules without changing the API.

## Credentials and external services

| Service or value | Required | Where it comes from | Where it belongs |
| --- | --- | --- | --- |
| `TWITCH_CLIENT_ID` | Yes, for IGDB | Twitch developer application | Cloudflare Worker secret |
| `TWITCH_CLIENT_SECRET` | Yes, for IGDB | Twitch developer application | Cloudflare Worker secret |
| GamerPower API key | No | GamerPower does not require authentication | Nowhere |
| `CLOUDFLARE_API_TOKEN` | Only for GitHub Actions deployment | Cloudflare API Tokens | GitHub Actions secret |
| `CLOUDFLARE_ACCOUNT_ID` | Only for GitHub Actions deployment | Cloudflare account details | GitHub Actions secret |
| `BACKEND_BASE_URL` | Yes, for app features | Deployed Worker URL | Local property or Actions secret |
| `CORS_ALLOWED_ORIGINS` | Only for hosted web | Website origins | Cloudflare Worker variable |

### IGDB and Twitch

IGDB uses Twitch application credentials. There is no additional IGDB API key, and the Worker obtains and refreshes
the OAuth access token automatically.

1. Create or sign in to a [Twitch account](https://www.twitch.tv/signup), verify its email address, and enable
   two-factor authentication.
2. Open the [Twitch Developer Console](https://dev.twitch.tv/console/apps) and select **Register Your Application**.
3. Enter a unique application name. For IGDB, use `http://localhost` as the OAuth redirect URL and select a suitable
   application category. Set the client type to **Confidential** when that option is shown.
4. Create the application, open **Manage**, and copy its **Client ID**.
5. Select **New Secret** and immediately copy the generated client secret. Generating another secret invalidates the
   previous one.
6. Store both values directly in the deployed Worker. Wrangler prompts for each value without writing it to Git:

   ```sh
   cd backend
   npx wrangler secret put TWITCH_CLIENT_ID
   npx wrangler secret put TWITCH_CLIENT_SECRET
   ```

Never put the client secret or generated OAuth token in `local.properties`, `wrangler.jsonc`, GitHub variables, or
mobile and browser builds. Follow the official [IGDB account guide](https://api-docs.igdb.com/#account-creation) and
[Twitch application guide](https://dev.twitch.tv/docs/authentication/register-app/) if their console changes.

### GamerPower

[GamerPower](https://www.gamerpower.com/api-read) is public and requires no account, API key, or authorization
header. Its terms require attribution with an active link to GamerPower. The Worker proxies its public API so every
Braindance client uses the same backend boundary.

### Cloudflare deployment credentials

Local deployment uses browser authentication and does not require manually creating an API token:

```sh
cd backend
npm ci
npx wrangler login
npm run deploy
```

GitHub Actions runs without an interactive login, so it needs two repository secrets:

1. In Cloudflare, open **My Profile → API Tokens**, select **Create Token**, and use the
   **Edit Cloudflare Workers** template. Restrict the token to the account that hosts this Worker.
2. Copy the token when Cloudflare displays it. This becomes `CLOUDFLARE_API_TOKEN`.
3. In **Workers & Pages**, copy **Account ID** from **Account Details**. This becomes
   `CLOUDFLARE_ACCOUNT_ID`. Cloudflare also documents other ways to
   [find the account ID](https://developers.cloudflare.com/fundamentals/account/find-account-and-zone-ids/).
4. In GitHub, open **Repository Settings → Secrets and variables → Actions → Secrets** and create secrets with
   those exact names.

The API token authorizes deployments and must remain secret. The account ID identifies the Cloudflare account; keep
it in Actions secrets as expected by the workflow. See Cloudflare's
[GitHub Actions authentication guide](https://developers.cloudflare.com/workers/ci-cd/external-cicd/github-actions/)
for the current permission requirements.

### URLs and browser CORS

After `npm run deploy`, Wrangler prints the Worker URL. This URL is configuration rather than a credential. Set it as
`BACKEND_BASE_URL` in the ignored root `local.properties`; for web publication, create a GitHub Actions secret with
the same name.

If a hosted browser client calls the Worker, deploy its exact origin as a regular Worker variable:

```sh
cd backend
npm run deploy -- --var 'CORS_ALLOWED_ORIGINS:https://example.com'
```

Use a comma-separated value for multiple origins. Do not add paths or trailing slashes. Localhost browser origins are
allowed automatically, and native Android and iOS clients do not use CORS.

## Deploy

1. Create the Twitch application and obtain the two IGDB credentials as described above.
2. From this directory, run `npm ci`, then `npx wrangler login` to connect your Cloudflare account.
3. Run `npm run deploy`. Wrangler creates the Worker and prints its public URL.
4. Add `TWITCH_CLIENT_ID` and `TWITCH_CLIENT_SECRET` with the `wrangler secret put` commands above. Each command
   publishes a new Worker version. `/healthz` returns 503 until both secrets are configured.
5. Set `BACKEND_BASE_URL` to the Worker URL in the ignored root `local.properties` for Android and iOS builds.

GitHub Actions deploys the Worker on direct pushes to `master`, after automatic promotion from `develop`, or
when the deployment workflow is manually run from `master`. Add `CLOUDFLARE_API_TOKEN` and
`CLOUDFLARE_ACCOUNT_ID` to the repository's Actions secrets before enabling deployment. The API token must have
permission to edit Workers for the target account.

You can also enter both secrets in **Workers & Pages → your Worker → Settings → Variables and Secrets** after
the first deployment. Later `npm run deploy` calls retain them. Do not put them in `wrangler.jsonc`, Git, or
mobile build settings. For local Worker development, use an ignored `backend/.dev.vars` file with the same two
variable names and run `npm run dev`.

Browser requests from `http://localhost` and `http://127.0.0.1`, on any port, are allowed for local development.
For a deployed web client, set `CORS_ALLOWED_ORIGINS` in the Cloudflare dashboard to a comma-separated list of exact
origins, for example `https://example.com,https://www.example.com`. Wrangler preserves dashboard variables during
deployment. The Worker handles `OPTIONS` preflight requests for `/v1/` routes.

The health endpoint is `GET /healthz`.

| Endpoint | Request |
| --- | --- |
| `POST /v1/games/details` | `{"id": 42}` |
| `POST /v1/games/anticipated` | `{"currentTimestamp": 1780000000, "pageSize": 20}` |
| `POST /v1/games/popular` | `{"ids": [42, 43], "pageSize": 20}` |
| `POST /v1/games/popularity` | `{"type": 34, "pageSize": 40}` |
| `GET /v1/giveaways` | No body |
| `GET /v1/giveaways/42` | No body |
| `GET /v1/giveaways/image?url=...` | GamerPower images referenced by giveaway responses |

POST requests use `Content-Type: application/json`. IDs and page sizes are bounded; the Worker generates IGDB
queries and GamerPower URLs itself. A single Durable Object coordinates the app token and the IGDB limit of four
upstream calls per second across Worker instances. Request and response sizes are capped; errors do not log
credentials or bodies. Durable Objects are available on Cloudflare's Free plan, subject to its limits.

The API is public to mobile clients. The global IGDB limit protects the upstream quota, but a third party can
still use all four slots. Configure Cloudflare rate limiting or other abuse controls before broad distribution.
Keep the Twitch secrets solely in Cloudflare. Rotate any secret previously included in a distributed client.

[Cloudflare deployment](https://developers.cloudflare.com/workers/get-started/guide/) ·
[Worker secrets](https://developers.cloudflare.com/workers/configuration/secrets/) ·
[Durable Objects](https://developers.cloudflare.com/durable-objects/get-started/) ·
[IGDB rate limit](https://api-docs.igdb.com/)

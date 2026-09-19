# Braindance API Worker

The Cloudflare Worker handles Twitch client credentials and IGDB requests. The mobile app calls four fixed JSON
endpoints; it never receives an OAuth token or the Twitch client secret. GamerPower remains a direct client request.

The request validation, IGDB queries, token lifecycle, response limits, and upstream error handling live in
`src/igdb-backend.mjs`. That module uses standard Web APIs and receives credentials, `fetch`, logging, time, and
rate-slot acquisition as dependencies. `worker.mjs` is the Cloudflare adapter: it supplies Worker secrets and a
Durable Object backed global rate limiter. A future Node.js adapter can reuse the core without changing the API.

## Deploy

1. Create a Twitch developer application by following the
   [IGDB authentication guide](https://api-docs.igdb.com/#account-creation) and keep its client ID and secret private.
2. From this directory, run `npm ci`, then `npx wrangler login` to connect your Cloudflare account.
3. Run `npm run deploy`. Wrangler creates the Worker and prints its public URL.
4. Add `TWITCH_CLIENT_ID` and `TWITCH_CLIENT_SECRET` as encrypted Worker secrets with
   `npx wrangler secret put TWITCH_CLIENT_ID` and `npx wrangler secret put TWITCH_CLIENT_SECRET`. Each command
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

The health endpoint is `GET /healthz`.

| Endpoint | JSON request |
| --- | --- |
| `POST /v1/games/details` | `{"id": 42}` |
| `POST /v1/games/anticipated` | `{"currentTimestamp": 1780000000, "pageSize": 20}` |
| `POST /v1/games/popular` | `{"ids": [42, 43], "pageSize": 20}` |
| `POST /v1/games/popularity` | `{"type": 34, "pageSize": 40}` |

All requests use `Content-Type: application/json`. IDs and page sizes are bounded; the Worker generates IGDB
queries itself. A single Durable Object coordinates the app token and the IGDB limit of four upstream calls per
second across Worker instances. Request and response sizes are capped; errors do not log credentials or bodies.
Durable Objects are available on Cloudflare's Free plan, subject to its limits.

The API is public to mobile clients. The global IGDB limit protects the upstream quota, but a third party can
still use all four slots. Configure Cloudflare rate limiting or other abuse controls before broad distribution.
Keep the Twitch secrets solely in Cloudflare. Rotate any secret previously included in a distributed client.

[Cloudflare deployment](https://developers.cloudflare.com/workers/get-started/guide/) ·
[Worker secrets](https://developers.cloudflare.com/workers/configuration/secrets/) ·
[Durable Objects](https://developers.cloudflare.com/durable-objects/get-started/) ·
[IGDB rate limit](https://api-docs.igdb.com/)

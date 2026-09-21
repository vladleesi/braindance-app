import { JSON_RESPONSE_HEADERS, jsonResponse, readLimited } from './http.mjs';

const giveawaysPath = '/v1/giveaways';
const giveawayPath = /^\/v1\/giveaways\/(\d+)$/;
const gamerPowerBaseUrl = 'https://www.gamerpower.com/api';
const maxResponseBytes = 2 * 1024 * 1024;

function upstreamUrl(path) {
  if (path === giveawaysPath) return `${gamerPowerBaseUrl}/giveaways`;

  const match = giveawayPath.exec(path);
  if (!match) return null;

  const id = Number(match[1]);
  if (!Number.isSafeInteger(id) || id <= 0 || id > 2147483647) return null;
  return `${gamerPowerBaseUrl}/giveaway?id=${id}`;
}

export function createGamerPowerBackend({
  fetchImpl = fetch,
  logger = console,
}) {
  return {
    async fetch(request) {
      if (request.method !== 'GET') return jsonResponse(404, { error: 'Not found' });

      const url = upstreamUrl(new URL(request.url).pathname);
      if (!url) return jsonResponse(404, { error: 'Not found' });

      try {
        const response = await fetchImpl(url, {
          headers: { Accept: 'application/json' },
          signal: AbortSignal.timeout(8000),
        });
        if (!response.ok) {
          logger.error('GamerPower request failed with status', response.status);
          if (response.status === 404) return jsonResponse(404, { error: 'Giveaway not found' });
          if (response.status === 429) return jsonResponse(429, { error: 'Rate limit exceeded' });
          return jsonResponse(502, { error: 'Upstream unavailable' });
        }
        if (!response.headers.get('content-type')?.includes('application/json')) {
          logger.error('GamerPower returned a non-JSON response');
          return jsonResponse(502, { error: 'Upstream unavailable' });
        }

        const body = await readLimited(response.body, maxResponseBytes);
        return new Response(body, { status: 200, headers: JSON_RESPONSE_HEADERS });
      } catch (error) {
        const message = error instanceof Error ? error.message : 'unknown_error';
        const name = error instanceof Error ? error.name : 'UnknownError';
        if (message !== 'body_too_large') logger.error('GamerPower gateway failed', name, message);
        return jsonResponse(502, { error: 'Upstream unavailable' });
      }
    },
  };
}

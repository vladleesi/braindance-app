import { JSON_RESPONSE_HEADERS, jsonResponse, readLimited } from './http.mjs';

const giveawaysPath = '/v1/giveaways';
const giveawayPath = /^\/v1\/giveaways\/(\d+)$/;
const giveawayImagePath = '/v1/giveaways/image';
const gamerPowerBaseUrl = 'https://www.gamerpower.com/api';
const gamerPowerOrigin = 'https://www.gamerpower.com';
const maxResponseBytes = 2 * 1024 * 1024;
const maxImageBytes = 5 * 1024 * 1024;

function imageUpstreamUrl(requestUrl) {
  const requestedUrl = new URL(requestUrl).searchParams.get('url');
  if (!requestedUrl) return null;

  try {
    const url = new URL(requestedUrl);
    if (url.origin !== gamerPowerOrigin || !url.pathname.startsWith('/offers/')) return null;
    return url;
  } catch {
    return null;
  }
}

function proxiedImageUrl(requestUrl, imageUrl) {
  if (!imageUrl) return imageUrl;
  const upstreamUrl = imageUpstreamUrl(`${requestUrl}?url=${encodeURIComponent(imageUrl)}`);
  if (!upstreamUrl) return imageUrl;

  const proxyUrl = new URL(giveawayImagePath, requestUrl);
  proxyUrl.searchParams.set('url', upstreamUrl.toString());
  return proxyUrl.toString();
}

function proxyGiveawayImages(payload, requestUrl) {
  const giveaways = Array.isArray(payload) ? payload : [payload];
  const proxied = giveaways.map(giveaway => ({
    ...giveaway,
    image: proxiedImageUrl(requestUrl, giveaway.image),
    thumbnail: proxiedImageUrl(requestUrl, giveaway.thumbnail),
  }));
  return Array.isArray(payload) ? proxied : proxied[0];
}

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

      const requestUrl = new URL(request.url);
      if (requestUrl.pathname === giveawayImagePath) {
        const imageUrl = imageUpstreamUrl(request.url);
        if (!imageUrl) return jsonResponse(400, { error: 'Invalid image URL' });

        try {
          const response = await fetchImpl(imageUrl, {
            headers: { Accept: 'image/*' },
            signal: AbortSignal.timeout(8000),
          });
          const contentType = response.headers.get('content-type');
          const contentLength = Number(response.headers.get('content-length'));
          if (!response.ok || !contentType?.startsWith('image/') || contentLength > maxImageBytes) {
            return jsonResponse(502, { error: 'Upstream image unavailable' });
          }

          const body = await response.arrayBuffer();
          if (body.byteLength > maxImageBytes) return jsonResponse(502, { error: 'Upstream image unavailable' });
          return new Response(body, {
            status: 200,
            headers: {
              'Content-Type': contentType,
              'Cache-Control': 'public, max-age=86400',
              'X-Content-Type-Options': 'nosniff',
            },
          });
        } catch (error) {
          const name = error instanceof Error ? error.name : 'UnknownError';
          logger.error('GamerPower image gateway failed', name);
          return jsonResponse(502, { error: 'Upstream image unavailable' });
        }
      }

      const url = upstreamUrl(requestUrl.pathname);
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
        const payload = proxyGiveawayImages(JSON.parse(body), request.url);
        return new Response(JSON.stringify(payload), { status: 200, headers: JSON_RESPONSE_HEADERS });
      } catch (error) {
        const message = error instanceof Error ? error.message : 'unknown_error';
        const name = error instanceof Error ? error.name : 'UnknownError';
        if (message !== 'body_too_large') logger.error('GamerPower gateway failed', name, message);
        return jsonResponse(502, { error: 'Upstream unavailable' });
      }
    },
  };
}

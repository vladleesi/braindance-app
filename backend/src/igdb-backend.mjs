const endpoints = new Set([
  '/v1/games/details',
  '/v1/games/anticipated',
  '/v1/games/popular',
  '/v1/games/popularity',
]);
const maxRequestBytes = 2048;
const maxResponseBytes = 1024 * 1024;
const responseHeaders = {
  'Content-Type': 'application/json; charset=utf-8',
  'Cache-Control': 'no-store',
  'X-Content-Type-Options': 'nosniff',
};

function json(status, body) {
  return new Response(JSON.stringify(body), { status, headers: responseHeaders });
}

async function readLimited(stream, limit) {
  if (!stream) return '';
  const reader = stream.getReader();
  const decoder = new TextDecoder();
  let size = 0;
  let text = '';
  while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    size += value.byteLength;
    if (size > limit) {
      await reader.cancel();
      throw new Error('body_too_large');
    }
    text += decoder.decode(value, { stream: true });
  }
  return text + decoder.decode();
}

function positiveInteger(value, max) {
  return Number.isSafeInteger(value) && value > 0 && value <= max;
}

function igdbQuery(path, input) {
  if (!input || typeof input !== 'object' || Array.isArray(input)) return null;
  switch (path) {
    case '/v1/games/details':
      if (!positiveInteger(input.id, 2147483647)) return null;
      return {
        path: '/v4/games',
        body: 'fields name,cover.url,similar_games.name,similar_games.cover.url,' +
          'videos.video_id,genres.name,summary,storyline,platforms.name,websites.url,' +
          'websites.type,screenshots.animated,screenshots.url,first_release_date,' +
          `release_dates.human;where id = ${input.id};limit 15;`,
      };
    case '/v1/games/anticipated':
      if (!positiveInteger(input.currentTimestamp, 4102444800) || !positiveInteger(input.pageSize, 50)) return null;
      return {
        path: '/v4/games',
        body: 'fields name,platforms.name,cover.url;' +
          `where first_release_date > ${input.currentTimestamp} & hypes > 0 & version_parent = null;` +
          `sort hypes desc;limit ${input.pageSize};`,
      };
    case '/v1/games/popular':
      if (!positiveInteger(input.pageSize, 50) || !Array.isArray(input.ids) || input.ids.length > 50 ||
          input.ids.length === 0 || !input.ids.every(id => positiveInteger(id, 2147483647))) return null;
      return {
        path: '/v4/games',
        body: 'fields name,platforms.name,cover.url;' +
          `where id = (${input.ids.join(', ')}) & age_ratings != null ` +
          '& age_ratings.rating_category != 7 & age_ratings.rating_category != 26 ' +
          `& age_ratings.rating_category != 38;sort hypes desc;limit ${input.pageSize};`,
      };
    case '/v1/games/popularity':
      if (input.type !== 34 || !positiveInteger(input.pageSize, 50)) return null;
      return {
        path: '/v4/popularity_primitives',
        body: `fields game_id;where popularity_type = 34;sort value desc;limit ${input.pageSize};`,
      };
    default:
      return null;
  }
}

export function healthResponse(credentialsAvailable) {
  return credentialsAvailable
    ? json(200, { ok: true })
    : json(503, { error: 'Backend credentials unavailable' });
}

export function createIgdbBackend({
  clientId,
  clientSecret,
  takeRequestSlot,
  fetchImpl = fetch,
  logger = console,
  now = Date.now,
}) {
  let cachedToken;
  let tokenExpiresAt = 0;
  let tokenRefresh;
  const credentialsAvailable = Boolean(clientId && clientSecret);

  async function getToken() {
    if (cachedToken && now() < tokenExpiresAt) return cachedToken;
    if (!tokenRefresh) {
      tokenRefresh = (async () => {
        const body = new URLSearchParams({
          client_id: clientId,
          client_secret: clientSecret,
          grant_type: 'client_credentials',
        });
        const response = await fetchImpl('https://id.twitch.tv/oauth2/token', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body,
          signal: AbortSignal.timeout(5000),
        });
        if (!response.ok) throw new Error('token_request_failed');
        const token = await response.json();
        if (typeof token.access_token !== 'string' || token.access_token.length === 0) {
          throw new Error('token_response_invalid');
        }
        const expiresIn = Number(token.expires_in);
        if (!Number.isFinite(expiresIn) || expiresIn <= 0) throw new Error('token_response_invalid');
        cachedToken = token.access_token;
        tokenExpiresAt = now() + Math.max(0, expiresIn - 60) * 1000;
        return cachedToken;
      })().finally(() => { tokenRefresh = undefined; });
    }
    return tokenRefresh;
  }

  async function callIgdb(query) {
    if (!await takeRequestSlot()) return { status: 429 };
    let token = await getToken();
    for (let attempt = 0; attempt < 2; attempt += 1) {
      if (attempt > 0 && !await takeRequestSlot()) return { status: 429 };
      const response = await fetchImpl(`https://api.igdb.com${query.path}`, {
        method: 'POST',
        headers: {
          'Client-ID': clientId,
          Authorization: `Bearer ${token}`,
          'Content-Type': 'text/plain',
        },
        body: query.body,
        signal: AbortSignal.timeout(8000),
      });
      if (response.status === 401 && attempt === 0) {
        cachedToken = undefined;
        tokenExpiresAt = 0;
        token = await getToken();
        continue;
      }
      if (!response.ok) {
        logger.error('IGDB request failed with status', response.status);
        return { status: response.status === 429 ? 429 : 502 };
      }
      if (!response.headers.get('content-type')?.includes('application/json')) {
        logger.error('IGDB returned a non-JSON response');
        return { status: 502 };
      }
      return { status: 200, body: await readLimited(response.body, maxResponseBytes) };
    }
    return { status: 502 };
  }

  return {
    async fetch(request) {
      const path = new URL(request.url).pathname;
      if (request.method !== 'POST' || !endpoints.has(path)) return json(404, { error: 'Not found' });
      if (!request.headers.get('content-type')?.startsWith('application/json')) {
        return json(415, { error: 'Expected application/json' });
      }
      if (!credentialsAvailable) return json(503, { error: 'Backend credentials unavailable' });

      try {
        const body = await readLimited(request.body, maxRequestBytes);
        let input;
        try {
          input = JSON.parse(body);
        } catch {
          return json(400, { error: 'Invalid JSON' });
        }
        const query = igdbQuery(path, input);
        if (!query) return json(400, { error: 'Invalid request' });
        const result = await callIgdb(query);
        if (result.status !== 200) {
          const error = result.status === 429 ? 'Rate limit exceeded' : 'Upstream unavailable';
          return json(result.status, { error });
        }
        return new Response(result.body, { status: 200, headers: responseHeaders });
      } catch (error) {
        const message = error instanceof Error ? error.message : 'unknown_error';
        const name = error instanceof Error ? error.name : 'UnknownError';
        if (message !== 'body_too_large') logger.error('IGDB gateway failed', name, message);
        return json(message === 'body_too_large' ? 413 : 502, {
          error: message === 'body_too_large' ? 'Query too large' : 'Upstream unavailable',
        });
      }
    },
  };
}

const allowedMethods = 'GET, POST, OPTIONS';
const allowedHeaders = 'Content-Type';

function configuredOrigins(value) {
  return new Set(
    value
      ?.split(',')
      .map(origin => origin.trim())
      .filter(Boolean) ?? [],
  );
}

function isLocalDevelopmentOrigin(origin) {
  try {
    const url = new URL(origin);
    return url.protocol === 'http:' && (url.hostname === 'localhost' || url.hostname === '127.0.0.1');
  } catch {
    return false;
  }
}

function isAllowedOrigin(origin, allowedOrigins) {
  return isLocalDevelopmentOrigin(origin) || configuredOrigins(allowedOrigins).has(origin);
}

function corsHeaders(origin) {
  return {
    'Access-Control-Allow-Origin': origin,
    'Access-Control-Allow-Methods': allowedMethods,
    'Access-Control-Allow-Headers': allowedHeaders,
    'Access-Control-Max-Age': '86400',
    Vary: 'Origin',
  };
}

export function preflightResponse(request, allowedOrigins) {
  const origin = request.headers.get('Origin');
  if (!origin || !isAllowedOrigin(origin, allowedOrigins)) {
    return new Response(null, { status: 403 });
  }
  return new Response(null, { status: 204, headers: corsHeaders(origin) });
}

export function withCors(request, response, allowedOrigins) {
  const origin = request.headers.get('Origin');
  if (!origin || !isAllowedOrigin(origin, allowedOrigins)) return response;

  const corsResponse = new Response(response.body, response);
  Object.entries(corsHeaders(origin)).forEach(([name, value]) => corsResponse.headers.set(name, value));
  return corsResponse;
}

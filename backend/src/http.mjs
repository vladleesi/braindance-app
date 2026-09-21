export const JSON_RESPONSE_HEADERS = {
  'Content-Type': 'application/json; charset=utf-8',
  'Cache-Control': 'no-store',
  'X-Content-Type-Options': 'nosniff',
};

export function jsonResponse(status, body) {
  return new Response(JSON.stringify(body), { status, headers: JSON_RESPONSE_HEADERS });
}

export async function readLimited(stream, limit) {
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

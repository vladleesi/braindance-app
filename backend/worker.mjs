import { DurableObject } from 'cloudflare:workers';
import { createIgdbBackend, healthResponse } from './src/igdb-backend.mjs';

export class IgdbGateway extends DurableObject {
  constructor(ctx, env) {
    super(ctx, env);
    this.ctx.storage.sql.exec('CREATE TABLE IF NOT EXISTS rate_limit (id INTEGER PRIMARY KEY, timestamps TEXT NOT NULL)');
    this.backend = createIgdbBackend({
      clientId: env.TWITCH_CLIENT_ID,
      clientSecret: env.TWITCH_CLIENT_SECRET,
      takeRequestSlot: () => this.takeRequestSlot(),
    });
  }

  takeRequestSlot() {
    const now = Date.now();
    const row = this.ctx.storage.sql.exec('SELECT timestamps FROM rate_limit WHERE id = 1').toArray()[0];
    const recent = row ? JSON.parse(row.timestamps).filter(timestamp => timestamp > now - 1000) : [];
    if (recent.length >= 4) return false;
    recent.push(now);
    this.ctx.storage.sql.exec(
      'INSERT INTO rate_limit (id, timestamps) VALUES (1, ?) ' +
        'ON CONFLICT(id) DO UPDATE SET timestamps = excluded.timestamps',
      JSON.stringify(recent),
    );
    return true;
  }

  fetch(request) {
    return this.backend.fetch(request);
  }
}

export default {
  fetch(request, env) {
    if (new URL(request.url).pathname === '/healthz' && request.method === 'GET') {
      return healthResponse(Boolean(env.TWITCH_CLIENT_ID && env.TWITCH_CLIENT_SECRET));
    }
    return env.IGDB_GATEWAY.getByName('global').fetch(request);
  },
};

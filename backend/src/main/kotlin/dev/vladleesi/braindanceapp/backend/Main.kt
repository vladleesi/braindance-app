package dev.vladleesi.braindanceapp.backend

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import redis.clients.jedis.JedisPooled

fun main() {
    val config = BackendConfig.fromEnvironment()
    config.publicBaseUrl?.let { base ->
        val uri = java.net.URI(base)
        require(
            uri.scheme in setOf("http", "https") &&
                uri.host != null &&
                uri.userInfo == null &&
                uri.path.isNullOrEmpty() &&
                uri.query == null &&
                uri.fragment == null,
        ) { "PUBLIC_BASE_URL must be an HTTP origin" }
    }
    require(config.rateLimiterMode in setOf("redis", "in-memory")) { "Invalid IGDB_RATE_LIMITER" }
    val redis =
        if (config.rateLimiterMode == "redis") {
            require(!config.redisUrl.isNullOrBlank()) { "REDIS_URL is required for the global IGDB limit" }
            JedisPooled(config.redisUrl)
        } else {
            null
        }
    val limiter = redis?.let(::RedisRateLimiter) ?: InMemoryRateLimiter()
    val client =
        HttpClient(CIO) {
            expectSuccess = false
            install(HttpTimeout)
        }
    val upstream = KtorUpstream(client)
    val igdb = IgdbService(upstream, limiter, config)
    val giveaways = GiveawayService(upstream)
    val server =
        embeddedServer(Netty, port = config.port, host = "0.0.0.0") {
            api(config, igdb, giveaways)
        }
    Runtime.getRuntime().addShutdownHook(
        Thread {
            server.stop()
            client.close()
            redis?.close()
        },
    )
    try {
        server.start(wait = true)
    } finally {
        client.close()
        redis?.close()
    }
}

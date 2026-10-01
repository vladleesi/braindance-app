@file:Suppress("ComplexCondition", "StringLiteralDuplication")

package dev.vladleesi.braindanceapp.backend

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.ResponseBodyReadyForSend
import io.ktor.server.application.install
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.util.AttributeKey
import java.net.URI
import java.security.MessageDigest

internal val publicGiveawayResponse = AttributeKey<Boolean>("PublicGiveawayResponse")

internal const val PRIVATE_CACHE_CONTROL = "private, no-store"
internal const val PUBLIC_CACHE_CONTROL = "public, max-age=60, stale-while-revalidate=30, stale-if-error=0"
internal const val ORIGIN_SECRET_HEADER = "X-Origin-Verify"
private const val MIN_ORIGIN_SECRET_LENGTH = 32

// Keep the Cloudflare expression in cloudflare.md in sync. Presence, including an empty value, bypasses cache.
internal val cacheBypassHeaders =
    setOf(
        "authorization",
        "proxy-authorization",
        "cookie",
        "origin",
        "x-api-key",
        "x-auth-token",
        "x-user-id",
        "x-session-id",
        "range",
        "transfer-encoding",
        "x-http-method-override",
        "x-http-method",
        "x-method-override",
        "x-forwarded-host",
        "x-host",
        "x-original-url",
        "x-rewrite-url",
        "forwarded",
    )

internal fun Application.installApiCachePolicy(config: BackendConfig) {
    require(!config.publicCacheEnabled || URI(config.publicBaseUrl.orEmpty()).scheme == "https") {
        "PUBLIC_CACHE_ENABLED requires an HTTPS PUBLIC_BASE_URL"
    }
    config.cloudflareOriginSecret?.let { secret ->
        require(secret.length >= MIN_ORIGIN_SECRET_LENGTH && secret.all { it in '!'..'~' }) {
            "CLOUDFLARE_ORIGIN_SECRET must contain at least 32 printable non-space ASCII characters"
        }
    }
    install(
        createApplicationPlugin("ApiCachePolicy") {
            on(ResponseBodyReadyForSend) { call, content ->
                val cacheable = call.canCachePublicResponse(config, content)
                call.response.headers.append(
                    HttpHeaders.CacheControl,
                    if (cacheable) PUBLIC_CACHE_CONTROL else PRIVATE_CACHE_CONTROL,
                )
                call.response.headers.append("X-Content-Type-Options", "nosniff")
                call.response.headers.append("Referrer-Policy", "no-referrer")
                call.response.headers.append("X-Frame-Options", "DENY")
                call.response.headers.append("Content-Security-Policy", "default-src 'none'; frame-ancestors 'none'")
            }
        },
    )
}

private fun ApplicationCall.canCachePublicResponse(
    config: BackendConfig,
    content: OutgoingContent,
): Boolean {
    // A route must explicitly attest to a public body. New routes are private even if they return GET/200.
    if (!config.publicCacheEnabled || attributes.getOrNull(publicGiveawayResponse) != true) return false
    if (request.httpMethod != HttpMethod.Get || request.uri != "/v1/giveaways") return false
    if (cacheBypassHeaders.any { request.headers.getAll(it) != null }) return false
    val lengths = request.headers.getAll(HttpHeaders.ContentLength)
    if (lengths != null && lengths != listOf("0")) return false
    val publicBaseUrl = config.publicBaseUrl ?: return false
    if (request.headers.getAll(HttpHeaders.Host) != listOf(URI(publicBaseUrl).rawAuthority)) return false
    if ((content.status ?: response.status()) != HttpStatusCode.OK) return false
    // Unknown response headers (cookies, identity, tokens, debug, Vary, existing cache directives) fail closed.
    val safeHeaders = setOf("content-type", "content-length")
    return (response.headers.allValues().names() + content.headers.names()).all { it.lowercase() in safeHeaders }
}

internal fun ApplicationCall.hasValidOriginSecret(config: BackendConfig): Boolean {
    val expected = config.cloudflareOriginSecret ?: return true
    val supplied = request.headers.getAll(ORIGIN_SECRET_HEADER)?.singleOrNull() ?: return false
    return MessageDigest.isEqual(expected.encodeToByteArray(), supplied.encodeToByteArray())
}

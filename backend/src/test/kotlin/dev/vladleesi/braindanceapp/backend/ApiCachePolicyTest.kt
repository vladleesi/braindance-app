package dev.vladleesi.braindanceapp.backend

import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class ApiCachePolicyTest {
    private val base = "https://localhost"
    private val config =
        BackendConfig("id", "secret", null, setOf("https://app.example"), 8080, base, publicCacheEnabled = true)

    private fun ApplicationTestBuilder.publicClient() =
        createClient {
            defaultRequest { header(HttpHeaders.Host, "localhost") }
        }

    @Test
    fun onlyAnonymousExactListCanBeCached() =
        testApplication {
            val client = publicClient()
            val remote = PublicUpstream()
            application { api(config, IgdbService(remote, RateLimiter { true }, config), GiveawayService(remote)) }
            val public = client.get("$base/v1/giveaways")
            assertEquals(HttpStatusCode.OK, public.status)
            assertEquals(PUBLIC_CACHE_CONTROL, public.headers[HttpHeaders.CacheControl])
            assertNull(public.headers[HttpHeaders.SetCookie])
            assertNull(public.headers[HttpHeaders.Authorization])
            assertNull(public.headers[ORIGIN_SECRET_HEADER])
            assertNull(public.headers[HttpHeaders.Vary])
            assertEquals("nosniff", public.headers["X-Content-Type-Options"])
            for (name in cacheBypassHeaders - "transfer-encoding") {
                val response = client.get("$base/v1/giveaways") { header(name, "sensitive-test-value") }
                assertEquals(PRIVATE_CACHE_CONTROL, response.headers[HttpHeaders.CacheControl], name)
                assertFalse(response.bodyAsText().contains("sensitive-test-value"), name)
            }
            val bodyBearing = client.get("$base/v1/giveaways") { setBody("sensitive-request-body") }
            assertEquals(PRIVATE_CACHE_CONTROL, bodyBearing.headers[HttpHeaders.CacheControl])
            for (name in listOf(HttpHeaders.Authorization, HttpHeaders.Cookie)) {
                val emptyCredential = client.get("$base/v1/giveaways") { header(name, "") }
                assertEquals(PRIVATE_CACHE_CONTROL, emptyCredential.headers[HttpHeaders.CacheControl], name)
            }
            val wrongHost = client.get("$base/v1/giveaways") { header(HttpHeaders.Host, "evil.example") }
            assertEquals(PRIVATE_CACHE_CONTROL, wrongHost.headers[HttpHeaders.CacheControl])
            assertFalse(wrongHost.bodyAsText().contains("evil.example"))
            for (path in listOf(
                "/v1/giveaways?token=secret",
                "/v1/giveaways?user_id=42",
                "/v1/giveaways?a=1&a=2",
                "/v1/giveaways/42",
                "/v1/giveaways/image?url=https%3A%2F%2Fwww.gamerpower.com%2Foffers%2Fa.png",
                "/v1/giveaways/file.css",
                "/v1/giveaways/",
                "/v1/auth",
                "/v1/favorites",
                "/v1/sync",
                "/health",
            )) {
                assertEquals(PRIVATE_CACHE_CONTROL, client.get("$base$path").headers[HttpHeaders.CacheControl], path)
            }
            val methods = listOf(HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch, HttpMethod.Delete, HttpMethod.Head)
            for (method in methods) {
                val response = client.request("$base/v1/giveaways") { this.method = method }
                assertEquals(PRIVATE_CACHE_CONTROL, response.headers[HttpHeaders.CacheControl], method.value)
            }
            val browser = client.get("$base/v1/giveaways") { header(HttpHeaders.Origin, "https://app.example") }
            assertEquals("https://app.example", browser.headers["Access-Control-Allow-Origin"])
            assertEquals(PRIVATE_CACHE_CONTROL, browser.headers[HttpHeaders.CacheControl])
            assertNull(browser.headers["Access-Control-Allow-Credentials"])
            val deniedBrowser =
                client.get("$base/v1/giveaways") {
                    header(HttpHeaders.Origin, "https://evil.example")
                }
            assertNull(deniedBrowser.headers["Access-Control-Allow-Origin"])
        }

    @Test
    fun defaultIsNoStoreAndHostCannotPoisonLinks() =
        testApplication {
            val client = publicClient()
            val disabled = config.copy(publicCacheEnabled = false)
            val remote = PublicUpstream()
            application { api(disabled, IgdbService(remote, RateLimiter { true }, disabled), GiveawayService(remote)) }
            assertEquals(PRIVATE_CACHE_CONTROL, client.get("$base/v1/giveaways").headers[HttpHeaders.CacheControl])
            val spoofed = client.get("$base/v1/giveaways") { header(HttpHeaders.Host, "evil.example") }
            assertEquals(PRIVATE_CACHE_CONTROL, spoofed.headers[HttpHeaders.CacheControl])
            assertFalse(spoofed.bodyAsText().contains("evil.example"))
        }

    @Test
    fun errorsAndRateLimitsAreNeverPublic() =
        testApplication {
            val client = publicClient()
            val remote = PublicUpstream()
            application { api(config, IgdbService(remote, RateLimiter { true }, config), GiveawayService(remote)) }
            for (status in listOf(401, 403, 404, 429, 500, 502, 503)) {
                remote.status = status
                val response = client.get("$base/v1/giveaways")
                assertEquals(PRIVATE_CACHE_CONTROL, response.headers[HttpHeaders.CacheControl], "$status")
            }
        }

    @Test
    fun extraResponseHeadersFailClosed() =
        testApplication {
            val client = publicClient()
            var extraHeader: String? = null
            var contentHeader = false
            application {
                installApiCachePolicy(config)
                routing {
                    get("/v1/giveaways") {
                        call.attributes.put(publicGiveawayResponse, true)
                        val name = extraHeader
                        if (contentHeader && name != null) {
                            call.respond(
                                object : OutgoingContent.ByteArrayContent() {
                                    override val headers = headersOf(name, "sensitive")
                                    override val status = HttpStatusCode.OK
                                    override val contentType = ContentType.Application.Json

                                    override fun bytes() = "[]".encodeToByteArray()
                                },
                            )
                        } else {
                            name?.let { call.response.headers.append(it, "sensitive") }
                            call.respondText("[]", ContentType.Application.Json, HttpStatusCode.OK)
                        }
                    }
                    get("/v1/user") {
                        call.respondText("""{"userId":42}""", ContentType.Application.Json, HttpStatusCode.OK)
                    }
                }
            }
            assertEquals(PUBLIC_CACHE_CONTROL, client.get("$base/v1/giveaways").headers[HttpHeaders.CacheControl])
            assertEquals(PRIVATE_CACHE_CONTROL, client.get("$base/v1/user").headers[HttpHeaders.CacheControl])
            for (inContent in listOf(false, true)) {
                contentHeader = inContent
                for (name in listOf("Set-Cookie", "Authorization", "X-User-Id", "X-Debug", "Vary", "Cache-Control")) {
                    extraHeader = name
                    val response = client.get("$base/v1/giveaways")
                    val cacheControl = checkNotNull(response.headers.getAll(HttpHeaders.CacheControl))
                    assertContains(cacheControl, PRIVATE_CACHE_CONTROL, name)
                }
            }
        }

    @Test
    fun originSecretRejectsBypassBeforeUpstream() =
        testApplication {
            val client = publicClient()
            val guarded = config.copy(cloudflareOriginSecret = "a".repeat(32))
            val remote = PublicUpstream()
            application { api(guarded, IgdbService(remote, RateLimiter { true }, guarded), GiveawayService(remote)) }
            for (value in listOf(null, "forged")) {
                val denied =
                    client.get("$base/v1/giveaways") {
                        value?.let { header(ORIGIN_SECRET_HEADER, it) }
                        header("CF-Connecting-IP", "1.2.3.4")
                    }
                assertEquals(HttpStatusCode.Forbidden, denied.status)
                assertEquals(PRIVATE_CACHE_CONTROL, denied.headers[HttpHeaders.CacheControl])
            }
            assertEquals(0, remote.calls)
            val duplicated =
                client.get("$base/v1/giveaways") {
                    header(ORIGIN_SECRET_HEADER, "a".repeat(32))
                    header(ORIGIN_SECRET_HEADER, "a".repeat(32))
                }
            assertEquals(HttpStatusCode.Forbidden, duplicated.status)
            assertEquals(0, remote.calls)
            val allowed = client.get("$base/v1/giveaways") { header(ORIGIN_SECRET_HEADER, "a".repeat(32)) }
            assertEquals(HttpStatusCode.OK, allowed.status)
            assertNull(allowed.headers[ORIGIN_SECRET_HEADER])
            assertEquals(1, remote.calls)
        }

    private class PublicUpstream : Upstream {
        var status = 200
        var calls = 0

        override suspend fun giveaway(url: String): UpstreamResponse {
            calls++
            return UpstreamResponse(
                HttpStatusCode.fromValue(status),
                headersOf(HttpHeaders.ContentType, "application/json"),
                """[{"image":"https://www.gamerpower.com/offers/a.png"}]""".encodeToByteArray(),
            )
        }

        override suspend fun image(url: String) =
            UpstreamResponse(HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "image/png"), byteArrayOf(1))

        override suspend fun token(
            clientId: String,
            clientSecret: String,
        ): UpstreamResponse = error("Unexpected token")

        override suspend fun igdb(
            path: String,
            query: String,
            clientId: String,
            token: String,
        ): UpstreamResponse = error("Unexpected IGDB call")
    }
}

package dev.vladleesi.braindanceapp.data.api

import dev.vladleesi.braindanceapp.BuildKonfig
import dev.vladleesi.braindanceapp.data.api.remote.AuthRemote
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import dev.vladleesi.braindanceapp.data.models.token.TwitchTokenResponse
import dev.vladleesi.braindanceapp.data.token.TokenStorage
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.retry
import io.ktor.client.plugins.timeout
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TIMEOUT_SECONDS = 5000L
private const val MAX_RETRIES = 5

val httpClient =
    HttpClient {
        expectSuccess = true
        engineConfig()
        authConfig()
        loggerConfig()
        jsonConfig()
        errorHandlerConfig()
        defaultRequestConfig()
    }.also { initNapier() }

fun HttpClientConfig<*>.engineConfig() {
    engine {
        request {
            timeout {
                requestTimeoutMillis = TIMEOUT_SECONDS
                socketTimeoutMillis = TIMEOUT_SECONDS
            }
            retry {
                retryOnExceptionOrServerErrors(maxRetries = MAX_RETRIES)
            }
        }
    }
}

fun HttpClientConfig<*>.authConfig() {
    install(Auth) {
        bearer {
            // TODO: Move to DI
            val authRemote = AuthRemote()
            val tokenStorage = TokenStorage()
            loadTokens {
                BearerTokens(accessToken = tokenStorage.getToken().orEmpty(), refreshToken = "")
            }
            refreshTokens {
                val twitchTokenResponse = authRemote.getToken().body<TwitchTokenResponse>()
                tokenStorage.saveToken(token = twitchTokenResponse.accessToken.orEmpty())
                BearerTokens(accessToken = tokenStorage.getToken().orEmpty(), refreshToken = "")
            }
        }
    }
}

fun HttpClientConfig<*>.loggerConfig() {
    install(Logging) {
        level = LogLevel.ALL
        logger =
            object : Logger {
                override fun log(message: String) {
                    Napier.d(tag = "HTTP Client", message = message)
                }
            }
    }
}

fun HttpClientConfig<*>.jsonConfig() {
    install(ContentNegotiation) {
        json(
            json =
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    explicitNulls = false
                    // Set default value to expected values
                    isLenient = true
                },
        )
    }
}

@Suppress("ForbiddenComment")
fun HttpClientConfig<*>.errorHandlerConfig() {
    HttpResponseValidator {
        handleResponseExceptionWithRequest { cause, _ ->
            // TODO: Handle it inside coroutines
            throw cause
        }
    }
}

fun HttpClientConfig<*>.defaultRequestConfig() {
    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = ApiConfig.IGDB_HOST
            contentType(ContentType.Application.Json)
        }
        header(ApiConfig.Headers.CLIENT_ID, BuildKonfig.CLIENT_ID)
    }
}

fun initNapier() {
    Napier.base(DebugAntilog())
}

package io.github.vladleesi.braindanceapp.api

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.vladleesi.braindanceapp.api.remote.AuthRemote
import io.github.vladleesi.braindanceapp.config.Config
import io.github.vladleesi.braindanceapp.config.Config.IGDB_URL
import io.github.vladleesi.braindanceapp.models.token.TwitchTokenResponse
import io.github.vladleesi.braindanceapp.storage.token.TokenStorage
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
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

private const val TIMEOUT_SECONDS = 5000L
private const val MAX_RETRIES = 5

const val HEADER_CLIENT_ID = "Client-ID"

val httpClient = HttpClient {
    expectSuccess = true
    loggerConfig()
    engineConfig()
    jsonConfig()
    tokenConfig()
    errorHandlerConfig()
    defaultRequestConfig()
}.also { initNapier() }

fun HttpClientConfig<*>.engineConfig() {
    engine {
        request {
            timeout {
                requestTimeoutMillis = TIMEOUT_SECONDS
            }
            retry {
                retryOnExceptionOrServerErrors(maxRetries = MAX_RETRIES)
            }
        }
    }
}

fun HttpClientConfig<*>.loggerConfig() {
    install(Logging) {
        level = LogLevel.ALL
        logger = object : Logger {
            override fun log(message: String) {
                Napier.d(tag = "HTTP Client", message = message)
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun HttpClientConfig<*>.jsonConfig() {
    install(ContentNegotiation) {
        json(
            json = Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                explicitNulls = false
                // Set default value to expected values
                isLenient = true
            }
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

fun HttpClientConfig<*>.tokenConfig() {
    install(Auth) {
        bearer {
            val remote = AuthRemote()
            val tokenStorage = TokenStorage()
            loadTokens {
                BearerTokens(tokenStorage.getToken().orEmpty(), "")
            }
            refreshTokens {
                val twitchTokenResponse = remote.getToken().body<TwitchTokenResponse>()
                val accessToken = twitchTokenResponse.accessToken
                tokenStorage.saveToken(accessToken)
                BearerTokens(tokenStorage.getToken().orEmpty(), "")
            }
        }
    }
}

fun HttpClientConfig<*>.defaultRequestConfig() {
    defaultRequest {
        url(IGDB_URL)
        headers {
            append(HEADER_CLIENT_ID, Config.CLIENT_ID)
        }
    }
}

fun initNapier() {
    Napier.base(DebugAntilog())
}

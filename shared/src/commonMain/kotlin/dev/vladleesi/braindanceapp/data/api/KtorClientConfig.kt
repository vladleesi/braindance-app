package dev.vladleesi.braindanceapp.data.api

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.retry
import io.ktor.client.plugins.timeout
import io.ktor.client.request.request
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TIMEOUT_SECONDS = 5000L
private const val MAX_RETRIES = 5

fun HttpClientConfig<*>.defaultConfig() {
    expectSuccess = true
    engineConfig()
    loggerConfig()
    jsonConfig()
    errorHandlerConfig()
}

fun initNapier() {
    Napier.base(DebugAntilog())
}

private fun HttpClientConfig<*>.engineConfig() {
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

private fun HttpClientConfig<*>.loggerConfig() {
    Logging {
        level = LogLevel.ALL
        logger =
            object : Logger {
                override fun log(message: String) {
                    Napier.d(tag = "HTTP Client", message = message)
                }
            }
    }
}

private fun HttpClientConfig<*>.jsonConfig() {
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
private fun HttpClientConfig<*>.errorHandlerConfig() {
    HttpResponseValidator {
        handleResponseExceptionWithRequest { cause, _ ->
            // TODO: Handle it inside coroutines
            throw cause
        }
    }
}

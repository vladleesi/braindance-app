package dev.vladleesi.braindanceapp.data.api.client

import dev.vladleesi.braindanceapp.data.api.engineConfig
import dev.vladleesi.braindanceapp.data.api.errorHandlerConfig
import dev.vladleesi.braindanceapp.data.api.jsonConfig
import dev.vladleesi.braindanceapp.data.api.loggerConfig
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType

val gamerPowerHttpClient =
    HttpClient {
        expectSuccess = true
        engineConfig()
        loggerConfig()
        jsonConfig()
        errorHandlerConfig()
        defaultRequestConfig()
    }

private fun HttpClientConfig<*>.defaultRequestConfig() {
    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = ApiConfig.Externals.GamerPower.HOST
            contentType(ContentType.Application.Json)
        }
    }
}

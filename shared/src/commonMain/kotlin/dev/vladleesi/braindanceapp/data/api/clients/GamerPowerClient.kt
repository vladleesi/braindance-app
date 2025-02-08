package dev.vladleesi.braindanceapp.data.api.clients

import dev.vladleesi.braindanceapp.data.api.defaultConfig
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType

object GamerPowerClient {
    fun build() =
        HttpClient {
            defaultConfig()
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
}

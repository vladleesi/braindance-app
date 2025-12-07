package dev.vladleesi.braindanceapp.data.api.clients

import dev.vladleesi.braindanceapp.BuildKonfig
import dev.vladleesi.braindanceapp.data.api.defaultConfig
import dev.vladleesi.braindanceapp.data.api.remote.AuthRemote
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import dev.vladleesi.braindanceapp.data.models.token.TwitchTokenResponse
import dev.vladleesi.braindanceapp.data.token.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType

object IgdbClient {
    fun build(
        authRemote: AuthRemote,
        tokenStorage: TokenStorage,
    ) = HttpClient {
        authConfig(authRemote, tokenStorage)
        defaultConfig()
        defaultRequestConfig()
    }
}

private fun HttpClientConfig<*>.defaultRequestConfig() {
    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = ApiConfig.HOST
            contentType(ContentType.Application.Json)
        }
        header(ApiConfig.Headers.CLIENT_ID, BuildKonfig.CLIENT_ID)
    }
}

private fun HttpClientConfig<*>.authConfig(
    authRemote: AuthRemote,
    tokenStorage: TokenStorage,
) {
    Auth {
        bearer {
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

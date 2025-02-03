package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.BuildKonfig
import dev.vladleesi.braindanceapp.data.api.httpClient
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

class AuthRemote {
    suspend fun getToken(): HttpResponse =
        httpClient.post(urlString = ApiConfig.TWITCH_OAUTH_TOKEN_URL) {
            url {
                parameters.append(ApiConfig.Headers.CLIENT_ID, BuildKonfig.CLIENT_ID)
                parameters.append("client_secret", BuildKonfig.CLIENT_SECRET)
                parameters.append("grant_type", "client_credentials")
            }
        }
}

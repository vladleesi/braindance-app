package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.BuildKonfig
import dev.vladleesi.braindanceapp.data.api.KtorClientManager
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

class AuthRemote(ktorClientManager: KtorClientManager) {
    private val twitchHttpClient = ktorClientManager.twitchHttpClient

    suspend fun getToken(): HttpResponse =
        twitchHttpClient.post(urlString = ApiConfig.Externals.Twitch.OAUTH_TOKEN_URL) {
            url {
                parameters.append("client_id", BuildKonfig.CLIENT_ID)
                parameters.append("client_secret", BuildKonfig.CLIENT_SECRET)
                parameters.append("grant_type", "client_credentials")
            }
        }
}

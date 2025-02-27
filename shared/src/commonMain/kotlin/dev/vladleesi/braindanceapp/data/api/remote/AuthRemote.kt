package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.BuildKonfig
import dev.vladleesi.braindanceapp.data.api.KtorClientManager
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

class AuthRemote(ktorClientManager: KtorClientManager) {
    private val twitchHttpClient = ktorClientManager.twitchHttpClient

    suspend fun getToken(): HttpResponse =
        twitchHttpClient.post(urlString = ApiConfig.Externals.Twitch.OAUTH_TOKEN_URL) {
            parameter(ApiConfig.Externals.Twitch.Params.CLIENT_ID, BuildKonfig.CLIENT_ID)
            parameter(ApiConfig.Externals.Twitch.Params.CLIENT_SECRET, BuildKonfig.CLIENT_SECRET)
            parameter(ApiConfig.Externals.Twitch.Params.GRANT_TYPE, "client_credentials")
        }
}

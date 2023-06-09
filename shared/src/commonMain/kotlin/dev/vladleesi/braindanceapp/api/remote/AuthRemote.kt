package dev.vladleesi.braindanceapp.api.remote

import dev.vladleesi.braindanceapp.api.HEADER_CLIENT_ID
import dev.vladleesi.braindanceapp.api.httpClient
import dev.vladleesi.braindanceapp.config.Config
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters

class AuthRemote {
    suspend fun getToken(): HttpResponse =
        httpClient.submitForm(
            url = Config.TWITCH_URL,
            formParameters = Parameters.build {
                append(HEADER_CLIENT_ID, Config.CLIENT_ID)
                append("client_secret", Config.CLIENT_SECRET)
                append("grant_type", "client_credentials")
            }
        )
}

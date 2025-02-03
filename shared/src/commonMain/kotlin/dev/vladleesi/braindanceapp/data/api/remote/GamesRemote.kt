package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.data.api.httpClient
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import dev.vladleesi.braindanceapp.data.models.request.RequestBody
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class GamesRemote {
    suspend fun games(requestBody: RequestBody): HttpResponse =
        httpClient.post(ApiConfig.Endpoints.GAMES) {
            setBody(requestBody.body)
        }

    suspend fun popularityPrimitives(requestBody: RequestBody): HttpResponse =
        httpClient.post(ApiConfig.Endpoints.POPULARITY_PRIMITIVES) {
            setBody(requestBody.body)
        }
}

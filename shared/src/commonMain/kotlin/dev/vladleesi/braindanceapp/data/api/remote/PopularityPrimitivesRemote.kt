package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.data.api.KtorClientManager
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import dev.vladleesi.braindanceapp.data.models.request.RequestBody
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class PopularityPrimitivesRemote(
    ktorClientManager: KtorClientManager,
) {
    private val igdbHttpClient = ktorClientManager.igdbHttpClient

    suspend fun popularityPrimitives(requestBody: RequestBody): HttpResponse =
        igdbHttpClient.post(ApiConfig.Endpoints.POPULARITY_PRIMITIVES) {
            setBody(requestBody.body)
        }
}

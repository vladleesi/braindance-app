package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.data.api.KtorClientManager
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import dev.vladleesi.braindanceapp.data.models.request.PopularityRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PopularityPrimitivesRemote(
    ktorClientManager: KtorClientManager,
) {
    private val backendHttpClient = ktorClientManager.backendHttpClient

    suspend fun popularityPrimitives(
        type: Int,
        pageSize: Int,
    ): HttpResponse =
        backendHttpClient.post(ApiConfig.Endpoints.POPULARITY_PRIMITIVES) {
            contentType(ContentType.Application.Json)
            setBody(PopularityRequest(type, pageSize))
        }
}

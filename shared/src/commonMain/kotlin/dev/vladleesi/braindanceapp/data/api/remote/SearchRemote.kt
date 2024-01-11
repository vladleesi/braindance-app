package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.data.api.httpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class SearchRemote {
    suspend fun search(query: String): HttpResponse =
        httpClient.get("games") {
            contentType(ContentType.Application.Json)
            url {
                parameters.append("search", query)
            }
        }
}

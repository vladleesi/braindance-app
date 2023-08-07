package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.data.api.httpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class SearchRemote {
    suspend fun search(query: String, limit: Int, fields: String): HttpResponse =
        httpClient.post("games") {
            setBody(
                """
                fields $fields;
                where name ~ "$query"*;
                limit $limit;
                """.trimIndent()
            )
        }
}

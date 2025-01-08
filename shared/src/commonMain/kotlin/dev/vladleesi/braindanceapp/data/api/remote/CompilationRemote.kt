package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.data.api.httpClient
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

class CompilationRemote {
    suspend fun popular(pageSize: Int): HttpResponse =
        httpClient.get("api/games/lists/popular") {
            url {
                parameter(ApiConfig.Query.DISCOVER, true.toString())
                parameter(ApiConfig.Query.PAGE_SIZE, pageSize.toString())
            }
        }

    suspend fun bestOfTheYear(
        pageSize: Int,
        year: Int,
    ): HttpResponse =
        httpClient.get("/api/games/lists/greatest") {
            url {
                parameter(ApiConfig.Query.DISCOVER, true.toString())
                parameter(ApiConfig.Query.PAGE_SIZE, pageSize.toString())
                parameter(ApiConfig.Query.YEAR, year.toString())
            }
        }
}

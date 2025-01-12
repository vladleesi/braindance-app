package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.data.api.httpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class StoresRemote {
    suspend fun stores(gameId: String): HttpResponse = httpClient.get("api/games/$gameId/stores")
}

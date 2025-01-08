package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.data.api.httpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class GameDetailsRemote {
    suspend fun gameDetails(gameId: String): HttpResponse = httpClient.get("/api/games/$gameId")
}

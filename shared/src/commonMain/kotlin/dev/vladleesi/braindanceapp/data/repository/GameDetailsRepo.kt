package dev.vladleesi.braindanceapp.data.repository

import dev.vladleesi.braindanceapp.data.api.remote.GameDetailsRemote
import dev.vladleesi.braindanceapp.data.models.games.GameDetailsResponse
import io.ktor.client.call.body

class GameDetailsRepo(private val gameDetailsRemote: GameDetailsRemote) {
    suspend fun gameDetails(gameId: String): GameDetailsResponse {
        val response = gameDetailsRemote.gameDetails(gameId)
        return response.body()
    }
}

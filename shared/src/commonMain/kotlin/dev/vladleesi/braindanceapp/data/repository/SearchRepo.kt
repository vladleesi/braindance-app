package dev.vladleesi.braindanceapp.data.repository

import dev.vladleesi.braindanceapp.data.api.remote.SearchRemote
import dev.vladleesi.braindanceapp.data.models.games.Game
import dev.vladleesi.braindanceapp.data.models.games.GameField
import io.ktor.client.call.body

class SearchRepo(private val searchRemote: SearchRemote) {

    suspend fun search(query: String): List<Game> {
        val fields = listOf(
            GameField.ID.fieldName,
            GameField.NAME.fieldName,
            GameField.PLATFORMS.fieldName,
            GameField.URL.fieldName
        ).joinToString(",")
        val response = searchRemote.search(query, LIMIT, fields)
        return response.body()
    }

    private companion object {
        private const val LIMIT = 10
    }
}

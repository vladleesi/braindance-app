package io.github.vladleesi.braindanceapp.repository

import io.github.vladleesi.braindanceapp.api.remote.SearchRemote
import io.github.vladleesi.braindanceapp.models.games.Game
import io.github.vladleesi.braindanceapp.models.games.GameField
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

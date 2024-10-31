package dev.vladleesi.braindanceapp.data.repository

import dev.vladleesi.braindanceapp.data.api.remote.SearchRemote
import dev.vladleesi.braindanceapp.data.models.games.GamesResponse
import io.ktor.client.call.body

class SearchRepo(private val searchRemote: SearchRemote) {
    suspend fun search(
        query: String,
        pageSize: Int,
    ): GamesResponse {
        val response = searchRemote.search(query, pageSize)
        return response.body()
    }
}

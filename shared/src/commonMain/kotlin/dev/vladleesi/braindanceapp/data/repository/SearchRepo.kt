package dev.vladleesi.braindanceapp.data.repository

import dev.vladleesi.braindanceapp.data.api.remote.SearchRemote
import dev.vladleesi.braindanceapp.data.models.search.SearchResult
import io.ktor.client.call.body

class SearchRepo(private val searchRemote: SearchRemote) {
    suspend fun search(query: String): SearchResult {
        val response = searchRemote.search(query)
        return response.body()
    }
}

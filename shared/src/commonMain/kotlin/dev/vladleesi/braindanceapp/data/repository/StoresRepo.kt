package dev.vladleesi.braindanceapp.data.repository

import dev.vladleesi.braindanceapp.data.api.remote.StoresRemote
import dev.vladleesi.braindanceapp.data.models.store.StoresResponse
import io.ktor.client.call.body

class StoresRepo(private val storesRemote: StoresRemote) {
    suspend fun stores(gameId: String): StoresResponse {
        val response = storesRemote.stores(gameId)
        return response.body()
    }
}

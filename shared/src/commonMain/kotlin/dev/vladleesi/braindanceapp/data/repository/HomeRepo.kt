package dev.vladleesi.braindanceapp.data.repository

import dev.vladleesi.braindanceapp.data.api.remote.CompilationRemote
import dev.vladleesi.braindanceapp.data.models.games.GamesResponse
import io.ktor.client.call.body

class HomeRepo(private val compilationRemote: CompilationRemote) {
    suspend fun bestOfTheYear(
        pageSize: Int,
        year: Int,
    ): GamesResponse {
        val response = compilationRemote.bestOfTheYear(pageSize, year)
        return response.body()
    }

    suspend fun popular(pageSize: Int): GamesResponse {
        val response = compilationRemote.popular(pageSize)
        return response.body()
    }
}

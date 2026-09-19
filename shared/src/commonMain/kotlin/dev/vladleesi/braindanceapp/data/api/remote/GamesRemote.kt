package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.data.api.KtorClientManager
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import dev.vladleesi.braindanceapp.data.models.request.GameDetailsRequest
import dev.vladleesi.braindanceapp.data.models.request.MostAnticipatedRequest
import dev.vladleesi.braindanceapp.data.models.request.PopularGamesRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GamesRemote(
    ktorClientManager: KtorClientManager,
) {
    private val igdbHttpClient = ktorClientManager.igdbHttpClient

    suspend fun gameDetails(id: Int): HttpResponse =
        igdbHttpClient.post(ApiConfig.Endpoints.GAME_DETAILS) {
            contentType(ContentType.Application.Json)
            setBody(GameDetailsRequest(id))
        }

    suspend fun mostAnticipated(
        currentTimestamp: Long,
        pageSize: Int,
    ): HttpResponse =
        igdbHttpClient.post(ApiConfig.Endpoints.MOST_ANTICIPATED) {
            contentType(ContentType.Application.Json)
            setBody(MostAnticipatedRequest(currentTimestamp, pageSize))
        }

    suspend fun popularGames(
        ids: List<Int>,
        pageSize: Int,
    ): HttpResponse =
        igdbHttpClient.post(ApiConfig.Endpoints.POPULAR_GAMES) {
            contentType(ContentType.Application.Json)
            setBody(PopularGamesRequest(ids, pageSize))
        }
}

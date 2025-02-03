package dev.vladleesi.braindanceapp.data.repository

import dev.vladleesi.braindanceapp.data.api.remote.GamesRemote
import dev.vladleesi.braindanceapp.data.models.games.GameItem
import dev.vladleesi.braindanceapp.data.models.popularity.PopularityResponse
import dev.vladleesi.braindanceapp.data.models.request.PopularityPrimitives
import dev.vladleesi.braindanceapp.data.models.request.RequestBodyBuilder
import io.ktor.client.call.body

class HomeRepo(private val gamesRemote: GamesRemote) {
    suspend fun mostAnticipated(
        pageSize: Int,
        currentTimestamp: Long,
    ): List<GameItem>? {
        val requestBodyBuilder =
            RequestBodyBuilder {
                fields = listOf("name", "platforms.name", "cover.url")
                where =
                    listOf(
                        "first_release_date > $currentTimestamp",
                        RequestBodyBuilder.Where.AND.operator,
                        "hypes > 0",
                        RequestBodyBuilder.Where.AND.operator,
                        "version_parent = null",
                    )
                sort = "hypes ${RequestBodyBuilder.Sort.DESC.order}"
                limit = pageSize
            }
        val response = gamesRemote.games(requestBody = requestBodyBuilder.build())
        return response.body()
    }

    suspend fun popularRightNow(pageSize: Int): List<GameItem>? {
        val requestBodyBuilderPopularity =
            RequestBodyBuilder {
                fields = listOf("game_id")
                where = listOf("popularity_type = ${PopularityPrimitives.VISITS.type}")
                sort = "value ${RequestBodyBuilder.Sort.DESC.order}"
                limit = pageSize
            }
        val popularityResponses =
            gamesRemote
                .popularityPrimitives(requestBody = requestBodyBuilderPopularity.build())
                .body<List<PopularityResponse>>()

        val gameIds = popularityResponses.map { "id = ${it.gameId}" }
        val requestBodyBuilderGames =
            RequestBodyBuilder {
                fields = listOf("name", "platforms.name", "cover.url")
                where =
                    listOf(
                        gameIds.joinToString(separator = " ${RequestBodyBuilder.Where.OR.operator} "),
                    )
                limit = pageSize
            }
        val response = gamesRemote.games(requestBody = requestBodyBuilderGames.build())
        return response.body()
    }
}

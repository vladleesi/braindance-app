package dev.vladleesi.braindanceapp.data.repository

import dev.vladleesi.braindanceapp.data.api.remote.GamesRemote
import dev.vladleesi.braindanceapp.data.models.games.GameItem
import dev.vladleesi.braindanceapp.data.models.request.PopularityPrimitives
import dev.vladleesi.braindanceapp.data.models.request.RequestBody
import dev.vladleesi.braindanceapp.utils.excludeAdultOnlyGames
import io.ktor.client.call.body

class HomeRepo(
    private val gamesRemote: GamesRemote,
    private val primitivesRepo: PopularityPrimitivesRepo,
) {
    suspend fun mostAnticipated(
        pageSize: Int,
        currentTimestamp: Long,
    ): List<GameItem>? {
        val builder =
            RequestBody.Builder {
                fields = listOf("name", "platforms.name", "cover.url")
                where =
                    listOf(
                        "first_release_date > $currentTimestamp",
                        RequestBody.Where.AND.operator,
                        "hypes > 0",
                        RequestBody.Where.AND.operator,
                        "version_parent = null",
                    )
                sort = "hypes ${RequestBody.Sort.DESC.order}"
                limit = pageSize
            }
        val response = gamesRemote.games(requestBody = builder.build())
        return response.body()
    }

    suspend fun popularRightNow(pageSize: Int): List<GameItem>? {
        val popularityResponses =
            primitivesRepo.popularityPrimitives(
                type = PopularityPrimitives.HOURS_WATCHED_24H,
                pageSize = pageSize + pageSize, // Extra results to compensate for filtered adult-only games
            )

        if (popularityResponses.isNullOrEmpty()) {
            return null
        }

        val gameIdsCondition =
            "id = (${popularityResponses.joinToString { it.gameId.toString() }})"

        return gamesRemote
            .games(
                requestBody =
                    RequestBody
                        .Builder {
                            fields = listOf("name", "platforms.name", "cover.url")
                            where =
                                listOf(
                                    gameIdsCondition,
                                    RequestBody.Where.AND.operator,
                                    excludeAdultOnlyGames(),
                                )
                            sort = "hypes ${RequestBody.Sort.DESC.order}"
                            limit = pageSize
                        }.build(),
            ).body()
    }
}

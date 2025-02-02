package dev.vladleesi.braindanceapp.data.repository

import dev.vladleesi.braindanceapp.data.api.remote.GamesRemote
import dev.vladleesi.braindanceapp.data.models.games.GameItem
import dev.vladleesi.braindanceapp.data.models.request.RequestBodyBuilder
import io.ktor.client.call.body

class GameDetailsRepo(private val gamesRemote: GamesRemote) {
    suspend fun gameDetails(gameId: Int): List<GameItem>? {
        val requestBodyBuilder =
            RequestBodyBuilder {
                fields =
                    listOf(
                        "name", "cover.url", "similar_games.name", "similar_games.cover.url",
                        "videos.video_id", "genres.name", "storyline", "platforms.name",
                        "websites.url", "screenshots.animated", "screenshots.url",
                    )
                where = listOf("id = $gameId")
            }
        val response = gamesRemote.games(requestBodyBuilder.build())
        return response.body()
    }
}

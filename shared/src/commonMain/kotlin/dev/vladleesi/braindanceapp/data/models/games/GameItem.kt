package dev.vladleesi.braindanceapp.data.models.games

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameItem(
    val cover: Cover?,
    @SerialName("first_release_date")
    val firstReleaseDate: Long?,
    val genres: List<Genre>?,
    val id: Int?,
    val name: String?,
    val platforms: List<Platform>?,
    val screenshots: List<Screenshot>?,
    @SerialName("similar_games")
    val similarGames: List<SimilarGame>?,
    val websites: List<Websites>?,
    val storyline: String?,
    val videos: List<Video>?,
)

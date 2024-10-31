package dev.vladleesi.braindanceapp.data.models.games

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GamesResponse(
    val count: String?,
    val results: List<Game>?,
)

@Serializable
data class Game(
    val name: String?,
    @SerialName("background_image")
    val backgroundImage: String?,
)

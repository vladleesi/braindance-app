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
    val id: Int?,
    val name: String?,
    @SerialName("background_image")
    val backgroundImageUrl: String?,
    val released: String?,
    val platforms: List<Platform>?,
)

@Serializable
data class Platform(
    @SerialName("platform")
    val details: PlatformDetails?,
    @SerialName("released_at")
    val releasedAt: String?,
    val requirements: Requirement?,
)

@Serializable
data class PlatformDetails(
    val id: String?,
    val slug: String?,
    val name: String?,
)

@Serializable
data class Requirement(
    val minimum: String?,
    val recommended: String?,
)

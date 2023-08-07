package dev.vladleesi.braindanceapp.data.models.games

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: String?,
    val name: String?,
//    val platforms: IntArray?,
    val url: String?,
    val websites: String?
)

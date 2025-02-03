package dev.vladleesi.braindanceapp.data.models.games

import kotlinx.serialization.Serializable

@Serializable
data class Platform(
    val id: Int?,
    val name: String?,
)

package dev.vladleesi.braindanceapp.data.models.games

import kotlinx.serialization.Serializable

@Serializable
data class Cover(
    val id: Int?,
    val url: String?,
)

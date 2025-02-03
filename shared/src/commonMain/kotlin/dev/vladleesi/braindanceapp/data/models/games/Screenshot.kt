package dev.vladleesi.braindanceapp.data.models.games

import kotlinx.serialization.Serializable

@Serializable
data class Screenshot(
    val animated: Boolean?,
    val id: Int?,
    val url: String?,
)

package dev.vladleesi.braindanceapp.data.models.games

import kotlinx.serialization.Serializable

@Serializable
data class ReleaseDate(
    val id: Int?,
    val human: String?,
)

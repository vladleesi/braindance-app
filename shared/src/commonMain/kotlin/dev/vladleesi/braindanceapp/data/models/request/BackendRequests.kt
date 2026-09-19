package dev.vladleesi.braindanceapp.data.models.request

import kotlinx.serialization.Serializable

@Serializable
data class GameDetailsRequest(
    val id: Int,
)

@Serializable
data class MostAnticipatedRequest(
    val currentTimestamp: Long,
    val pageSize: Int,
)

@Serializable
data class PopularGamesRequest(
    val ids: List<Int>,
    val pageSize: Int,
)

@Serializable
data class PopularityRequest(
    val type: Int,
    val pageSize: Int,
)

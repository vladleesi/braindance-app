package dev.vladleesi.braindanceapp.data.models.store

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreDetails(
    @SerialName("game_id")
    val gameId: Int,
    val id: Int,
    @SerialName("store_id")
    val storeId: Int,
    val url: String,
)

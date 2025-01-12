package dev.vladleesi.braindanceapp.data.models.store

import kotlinx.serialization.Serializable

@Serializable
data class StoresResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<StoreDetails>?,
)

package dev.vladleesi.braindanceapp.data.repository

import dev.vladleesi.braindanceapp.data.api.remote.PopularityPrimitivesRemote
import dev.vladleesi.braindanceapp.data.models.popularity.PopularityResponse
import dev.vladleesi.braindanceapp.data.models.request.PopularityPrimitives
import dev.vladleesi.braindanceapp.data.models.request.RequestBody
import io.ktor.client.call.body

class PopularityPrimitivesRepo(
    private val remote: PopularityPrimitivesRemote,
) {
    suspend fun popularityPrimitives(
        type: PopularityPrimitives,
        pageSize: Int,
    ) = remote
        .popularityPrimitives(
            requestBody =
                RequestBody
                    .Builder {
                        fields = listOf("game_id")
                        where = listOf("popularity_type = ${type.type}")
                        sort = "value ${RequestBody.Sort.DESC.order}"
                        limit = pageSize
                    }.build(),
        ).body<List<PopularityResponse>?>()
}

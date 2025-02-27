package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.data.api.KtorClientManager
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

class GamerPowerRemote(ktorClientManager: KtorClientManager) {
    private val gamerPowerHttpClient = ktorClientManager.gamerPowerHttpClient

    suspend fun giveaways(): HttpResponse = gamerPowerHttpClient.get(ApiConfig.Externals.GamerPower.Endpoints.GIVEAWAYS)

    suspend fun giveaway(id: Int): HttpResponse =
        gamerPowerHttpClient.get(ApiConfig.Externals.GamerPower.Endpoints.GIVEAWAY) {
            parameter(ApiConfig.Externals.GamerPower.Params.ID, id)
        }
}

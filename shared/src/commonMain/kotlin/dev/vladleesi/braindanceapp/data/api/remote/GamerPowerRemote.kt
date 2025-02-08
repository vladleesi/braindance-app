package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.data.api.KtorClientManager
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

class GamerPowerRemote(ktorClientManager: KtorClientManager) {
    private val gamerPowerHttpClient = ktorClientManager.gamerPowerHttpClient

    suspend fun giveaway(): HttpResponse = gamerPowerHttpClient.post(ApiConfig.Externals.GamerPower.Endpoints.GIVEAWAYS)
}

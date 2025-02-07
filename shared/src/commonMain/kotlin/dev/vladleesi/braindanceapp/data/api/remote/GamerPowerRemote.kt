package dev.vladleesi.braindanceapp.data.api.remote

import dev.vladleesi.braindanceapp.data.api.client.gamerPowerHttpClient
import dev.vladleesi.braindanceapp.data.config.ApiConfig
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

class GamerPowerRemote {
    suspend fun giveaway(): HttpResponse = gamerPowerHttpClient.post(ApiConfig.Externals.GamerPower.Endpoints.GIVEAWAYS)
}

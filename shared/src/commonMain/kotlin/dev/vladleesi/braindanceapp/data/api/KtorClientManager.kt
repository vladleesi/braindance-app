package dev.vladleesi.braindanceapp.data.api

import io.ktor.client.HttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class KtorClientManager : KoinComponent {
    val igdbHttpClient: HttpClient by inject(named(IGDB_HTTP_CLIENT))
    val twitchHttpClient: HttpClient by inject(named(TWITCH_HTTP_CLIENT))
    val gamerPowerHttpClient: HttpClient by inject(named(GAMER_POWER_HTTP_CLIENT))

    companion object {
        const val IGDB_HTTP_CLIENT = "IGDB_HTTP_CLIENT"
        const val TWITCH_HTTP_CLIENT = "TWITCH_HTTP_CLIENT"
        const val GAMER_POWER_HTTP_CLIENT = "GAMER_POWER_HTTP_CLIENT"
    }
}

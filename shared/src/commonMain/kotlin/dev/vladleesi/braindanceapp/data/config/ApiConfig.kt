package dev.vladleesi.braindanceapp.data.config

object ApiConfig {
    const val IGDB_HOST = "api.igdb.com"
    const val TWITCH_OAUTH_TOKEN_URL = "https://id.twitch.tv/oauth2/token"

    object Headers {
        const val CLIENT_ID = "Client-ID"
    }

    object Endpoints {
        const val GAMES = "v4/games"
        const val POPULARITY_PRIMITIVES = "v4/popularity_primitives"
    }
}

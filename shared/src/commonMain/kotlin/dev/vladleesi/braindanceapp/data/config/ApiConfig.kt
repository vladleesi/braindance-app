package dev.vladleesi.braindanceapp.data.config

object ApiConfig {
    const val HOST = "api.igdb.com"

    object Headers {
        const val CLIENT_ID = "Client-ID"
    }

    object Endpoints {
        const val GAMES = "v4/games"
        const val POPULARITY_PRIMITIVES = "v4/popularity_primitives"
    }

    object Externals {
        object Twitch {
            const val OAUTH_TOKEN_URL = "https://id.twitch.tv/oauth2/token"
        }

        object GamerPower {
            const val HOST = "www.gamerpower.com"

            object Endpoints {
                const val GIVEAWAYS = "api/giveaways"
            }
        }
    }
}

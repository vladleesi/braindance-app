package dev.vladleesi.braindanceapp.data.config

object ApiConfig {
    object Endpoints {
        const val GAME_DETAILS = "v1/games/details"
        const val MOST_ANTICIPATED = "v1/games/anticipated"
        const val POPULAR_GAMES = "v1/games/popular"
        const val POPULARITY_PRIMITIVES = "v1/games/popularity"
    }

    object Externals {
        object GamerPower {
            const val HOST = "www.gamerpower.com"

            object Endpoints {
                const val GIVEAWAYS = "api/giveaways"
                const val GIVEAWAY = "api/giveaway"
            }

            object Params {
                const val ID = "id"
            }
        }
    }
}

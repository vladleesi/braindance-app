package dev.vladleesi.braindanceapp.utils

import dev.vladleesi.braindanceapp.data.models.games.Websites
import dev.vladleesi.braindanceapp.resources.Res
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_android
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_ios
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_playstation
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_switch
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_windows
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_xbox
import dev.vladleesi.braindanceapp.resources.ic_store_logo_epic_games
import dev.vladleesi.braindanceapp.resources.ic_store_logo_gog
import dev.vladleesi.braindanceapp.resources.ic_store_logo_steam
import org.jetbrains.compose.resources.DrawableResource

fun List<Websites>.storeTypes(): List<StoreTypeModel> {
    return this.mapNotNull { it.storeType() }
}

/**
 * Determines the store type based on the URL and returns a [StoreTypeModel].
 */
fun Websites.storeType(): StoreTypeModel? {
    val url = url.orEmpty()
    val storeType =
        StoreType.entries.firstOrNull { type ->
            url.contains(type.host, ignoreCase = true)
        }
    return storeType?.toStoreModel(url)
}

private fun StoreType.toStoreModel(url: String): StoreTypeModel {
    return StoreTypeModel(
        name = this.storeName,
        image = this.image,
        url = url,
    )
}

enum class StoreType(val host: String, val storeName: String, val image: DrawableResource?) {
    PlayStationStore("playstation.com", "PlayStation Store", Res.drawable.ic_platform_logo_playstation),
    MicrosoftStore("microsoft.com", "Microsoft Store", Res.drawable.ic_platform_logo_windows),
    XboxStore("xbox.com", "Xbox Store", Res.drawable.ic_platform_logo_xbox),
    AppStore("apple.com", "App Store", Res.drawable.ic_platform_logo_ios),
    NintendoStore("nintendo.com", "Nintendo Store", Res.drawable.ic_platform_logo_switch),
    GooglePlay("google.com", "Google Play", Res.drawable.ic_platform_logo_android),
    Steam("steampowered.com", "Steam", Res.drawable.ic_store_logo_steam),
    GOG("gog.com", "GOG", Res.drawable.ic_store_logo_gog),
    EpicGamesStore("epicgames.com", "Epic Games", Res.drawable.ic_store_logo_epic_games),

    // TODO: Add other stores
}

data class StoreTypeModel(
    val name: String,
    val image: DrawableResource?,
    val url: String,
)

package dev.vladleesi.braindanceapp.utils

import dev.vladleesi.braindanceapp.data.models.store.StoreDetails
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
import dev.vladleesi.braindanceapp.utils.StoreType.AppStore
import dev.vladleesi.braindanceapp.utils.StoreType.EpicGamesStore
import dev.vladleesi.braindanceapp.utils.StoreType.GOG
import dev.vladleesi.braindanceapp.utils.StoreType.GooglePlay
import dev.vladleesi.braindanceapp.utils.StoreType.MicrosoftStore
import dev.vladleesi.braindanceapp.utils.StoreType.NintendoStore
import dev.vladleesi.braindanceapp.utils.StoreType.PlayStationStore
import dev.vladleesi.braindanceapp.utils.StoreType.Steam
import dev.vladleesi.braindanceapp.utils.StoreType.Unknown
import dev.vladleesi.braindanceapp.utils.StoreType.XboxStore
import org.jetbrains.compose.resources.DrawableResource

fun List<StoreDetails>.storeTypes(): List<StoreTypeModel> {
    return this.map { it.storeType() }
}

/**
 * Determines the store type based on the URL and returns a [StoreTypeModel].
 */
fun StoreDetails.storeType(): StoreTypeModel {
    val storeMappings =
        mapOf(
            "playstation.com" to PlayStationStore,
            "microsoft.com" to MicrosoftStore,
            "xbox.com" to XboxStore,
            "apple.com" to AppStore,
            "nintendo.com" to NintendoStore,
            "google.com" to GooglePlay,
            "steampowered.com" to Steam,
            "gog.com" to GOG,
            "epicgames.com" to EpicGamesStore,
        )

    val matchingEntry = storeMappings.entries.firstOrNull { url.contains(it.key, ignoreCase = true) }
    val store = matchingEntry?.value ?: Unknown

    return store.toStoreModel(url)
}

private fun StoreType.toStoreModel(url: String): StoreTypeModel {
    return StoreTypeModel(
        name = this.storeName,
        image = this.image,
        url = url,
    )
}

enum class StoreType(val storeName: String, val image: DrawableResource?) {
    PlayStationStore("PlayStation Store", Res.drawable.ic_platform_logo_playstation),
    MicrosoftStore("Microsoft Store", Res.drawable.ic_platform_logo_windows),
    XboxStore("Xbox Store", Res.drawable.ic_platform_logo_xbox),
    AppStore("App Store", Res.drawable.ic_platform_logo_ios),
    NintendoStore("Nintendo Store", Res.drawable.ic_platform_logo_switch),
    GooglePlay("Google Play", Res.drawable.ic_platform_logo_android),
    Steam("Steam", Res.drawable.ic_store_logo_steam),
    GOG("GOG", Res.drawable.ic_store_logo_gog),
    EpicGamesStore("Epic Games", Res.drawable.ic_store_logo_epic_games),
    Unknown("Unknown", null),
}

data class StoreTypeModel(
    val name: String,
    val image: DrawableResource?,
    val url: String,
)

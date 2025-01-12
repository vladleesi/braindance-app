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

fun List<StoreDetails>.storeTypes(): List<Pair<StoreType, String>> {
    return this.map { it.storeType() }
}

fun StoreDetails.storeType(): Pair<StoreType, String> {
    return when {
        url.contains("playstation.com", ignoreCase = true) -> PlayStationStore to url
        url.contains("microsoft.com", ignoreCase = true) -> MicrosoftStore to url
        url.contains("xbox.com", ignoreCase = true) -> XboxStore to url
        url.contains("apple.com", ignoreCase = true) -> AppStore to url
        url.contains("nintendo.com", ignoreCase = true) -> NintendoStore to url
        url.contains("google.com", ignoreCase = true) -> GooglePlay to url
        url.contains("steampowered.com", ignoreCase = true) -> Steam to url
        url.contains("gog.com", ignoreCase = true) -> GOG to url
        url.contains("epicgames.com", ignoreCase = true) -> EpicGamesStore to url
        else -> Unknown to url
    }
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

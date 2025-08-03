package dev.vladleesi.braindanceapp.utils

import dev.vladleesi.braindanceapp.data.models.games.ExternalGames
import dev.vladleesi.braindanceapp.data.models.games.ExternalGamesCategory
import dev.vladleesi.braindanceapp.data.models.games.WebsiteCategory
import dev.vladleesi.braindanceapp.data.models.games.Websites
import dev.vladleesi.braindanceapp.resources.Res
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_android
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_ios
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_playstation
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_windows
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_xbox
import dev.vladleesi.braindanceapp.resources.ic_store_logo_epic_games
import dev.vladleesi.braindanceapp.resources.ic_store_logo_gog
import dev.vladleesi.braindanceapp.resources.ic_store_logo_steam
import org.jetbrains.compose.resources.DrawableResource

fun getMergedStoresType(
    externalGames: List<ExternalGames>,
    websites: List<Websites>,
): List<StoreTypeModel> {
    val externalGamesStores = externalGames.toStoreTypes()
    val websitesStores = websites.toStoreTypesFiltered(externalGamesStores)
    return externalGamesStores + websitesStores
}

data class StoreTypeModel(
    val name: String,
    val image: DrawableResource?,
    val url: String,
)

fun List<ExternalGames>.toStoreTypes(): List<StoreTypeModel> =
    this
        .uniqueExternalGamesByCategory()
        .mapNotNull { it.storeType() }

fun List<Websites>.toStoreTypesFiltered(externalStores: List<StoreTypeModel>): List<StoreTypeModel> =
    this
        .validWebsites()
        .mapNotNull { it.storeType() }
        .filterNot { site -> externalStores.any { it.name == site.name } }

private fun ExternalGames.storeType(): StoreTypeModel? {
    val url = url?.takeIf { deprecatedStoreUrls.none(it::contains) } ?: return null
    val storeType =
        StoreType.entries.firstOrNull { type ->
            type.externalGamesCategory == this.category
        }
    return storeType?.toStoreModel(url)
}

private fun Websites.storeType(): StoreTypeModel? {
    val url = url.orEmpty()
    val storeType =
        StoreType.entries.firstOrNull { type ->
            type.websiteCategory == this.category
        }
    return storeType?.toStoreModel(url)
}

private enum class StoreType(
    val externalGamesCategory: ExternalGamesCategory,
    val websiteCategory: WebsiteCategory? = null,
    val storeName: String,
    val image: DrawableResource?,
) {
    PlayStationStore(
        externalGamesCategory = ExternalGamesCategory.PLAYSTATION_STORE_US,
        storeName = "PlayStation Store",
        image = Res.drawable.ic_platform_logo_playstation,
    ),
    MicrosoftStore(
        externalGamesCategory = ExternalGamesCategory.MICROSOFT,
        storeName = "Microsoft Store",
        image = Res.drawable.ic_platform_logo_windows,
    ),
    XboxStore(
        externalGamesCategory = ExternalGamesCategory.XBOX_MARKETPLACE,
        storeName = "Xbox Store",
        image = Res.drawable.ic_platform_logo_xbox,
    ),
    AppStore(
        externalGamesCategory = ExternalGamesCategory.APPLE,
        websiteCategory = WebsiteCategory.IPHONE,
        storeName = "App Store",
        image = Res.drawable.ic_platform_logo_ios,
    ),
    GooglePlay(
        externalGamesCategory = ExternalGamesCategory.ANDROID,
        websiteCategory = WebsiteCategory.ANDROID,
        storeName = "Google Play",
        image = Res.drawable.ic_platform_logo_android,
    ),
    Steam(
        externalGamesCategory = ExternalGamesCategory.STEAM,
        websiteCategory = WebsiteCategory.STEAM,
        storeName = "Steam",
        image = Res.drawable.ic_store_logo_steam,
    ),
    GOG(
        externalGamesCategory = ExternalGamesCategory.GOG,
        websiteCategory = WebsiteCategory.GOG,
        storeName = "GOG",
        image = Res.drawable.ic_store_logo_gog,
    ),
    EpicGamesStore(
        externalGamesCategory = ExternalGamesCategory.EPIC_GAME_STORE,
        websiteCategory = WebsiteCategory.EPIC_GAMES,
        storeName = "Epic Games",
        image = Res.drawable.ic_store_logo_epic_games,
    ),
}

private fun List<ExternalGames>.uniqueExternalGamesByCategory(): List<ExternalGames> =
    this
        .filter { it.url != null && deprecatedStoreUrls.none { url -> it.url.contains(url) } }
        .groupBy { it.category }
        .map { it.value.first() }

private fun List<Websites>.validWebsites(): List<Websites> =
    this.filter { !it.url.isNullOrEmpty() && deprecatedStoreUrls.none { url -> it.url.contains(url) } }

private fun StoreType.toStoreModel(url: String): StoreTypeModel =
    StoreTypeModel(
        name = this.storeName,
        image = this.image,
        url = url,
    )

private val deprecatedStoreUrls =
    hashSetOf(
        "marketplace.xbox.com",
    )

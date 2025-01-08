package dev.vladleesi.braindanceapp.data.models.games

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameDetailsResponse(
    val id: Int?,
    val slug: String?,
    val name: String?,
    @SerialName("name_original")
    val nameOriginal: String?,
    val description: String?,
    @SerialName("description_raw")
    val descriptionRaw: String?,
    val metacritic: Int?,
//    @SerialName("metacritic_platforms")
//    val metacriticPlatforms: List<Any>?,
    val released: String?,
    val tba: Boolean?,
    val updated: String?,
    @SerialName("background_image")
    val backgroundImage: String?,
    @SerialName("background_image_additional")
    val backgroundImageAdditional: String?,
    val website: String?,
    val rating: Double?,
    @SerialName("rating_top")
    val ratingTop: Int?,
    val ratings: List<Rating>?,
//    val reactions: Map<String, Any>?,
    val added: Int?,
    @SerialName("added_by_status")
    val addedByStatus: AddedByStatus?,
    val playtime: Int?,
    @SerialName("screenshots_count")
    val screenshotsCount: Int?,
    @SerialName("movies_count")
    val moviesCount: Int?,
    @SerialName("creators_count")
    val creatorsCount: Int?,
    @SerialName("achievements_count")
    val achievementsCount: Int?,
    @SerialName("parent_achievements_count")
    val parentAchievementsCount: Int?,
    @SerialName("reddit_url")
    val redditUrl: String?,
    @SerialName("reddit_name")
    val redditName: String?,
    @SerialName("reddit_description")
    val redditDescription: String?,
    @SerialName("reddit_logo")
    val redditLogo: String?,
    @SerialName("reddit_count")
    val redditCount: Int?,
    @SerialName("twitch_count")
    val twitchCount: Int?,
    @SerialName("youtube_count")
    val youtubeCount: Int?,
    @SerialName("reviews_text_count")
    val reviewsTextCount: Int?,
    @SerialName("ratings_count")
    val ratingsCount: Int?,
    @SerialName("suggestions_count")
    val suggestionsCount: Int?,
    @SerialName("alternative_names")
    val alternativeNames: List<String>?,
    @SerialName("metacritic_url")
    val metacriticUrl: String?,
    @SerialName("parents_count")
    val parentsCount: Int?,
    @SerialName("additions_count")
    val additionsCount: Int?,
    @SerialName("game_series_count")
    val gameSeriesCount: Int?,
//    @SerialName("user_game")
//    val userGame: Any?,
    @SerialName("reviews_count")
    val reviewsCount: Int?,
    @SerialName("saturated_color")
    val saturatedColor: String?,
    @SerialName("dominant_color")
    val dominantColor: String?,
    @SerialName("parent_platforms")
    val parentPlatforms: List<Platform>?,
    val platforms: List<Platform>?,
    val stores: List<StoreDetail>?,
    val developers: List<Developer>?,
    val genres: List<Genre>?,
    val tags: List<Tag>?,
)

@Serializable
data class Rating(
    val id: Int?,
    val title: String?,
    val count: Int?,
    val percent: Double?,
)

@Serializable
data class AddedByStatus(
    val yet: Int?,
    val owned: Int?,
    val beaten: Int?,
    val toplay: Int?,
    val dropped: Int?,
    val playing: Int?,
)

@Serializable
data class StoreDetail(
    val id: Int?,
    val url: String?,
    val store: Store?,
)

@Serializable
data class Store(
    val id: Int?,
    val name: String?,
    val slug: String?,
    val domain: String?,
    @SerialName("games_count")
    val gamesCount: Int?,
    @SerialName("image_background")
    val imageBackground: String?,
)

@Serializable
data class Developer(
    val id: Int?,
    val name: String?,
    val slug: String?,
    @SerialName("games_count")
    val gamesCount: Int?,
    @SerialName("image_background")
    val imageBackground: String?,
)

@Serializable
data class Genre(
    val id: Int?,
    val name: String?,
    val slug: String?,
    @SerialName("games_count")
    val gamesCount: Int?,
    @SerialName("image_background")
    val imageBackground: String?,
)

@Serializable
data class Tag(
    val id: Int?,
    val name: String?,
    val slug: String?,
    val language: String?,
    @SerialName("games_count")
    val gamesCount: Int?,
    @SerialName("image_background")
    val imageBackground: String?,
)

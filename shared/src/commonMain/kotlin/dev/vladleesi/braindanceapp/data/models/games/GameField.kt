package dev.vladleesi.braindanceapp.data.models.games

enum class GameField {
    ARTWORKS,
    COLLECTION,
    COVER,
    DLCS,
    EXPANSIONS,
    FRANCHISE,
    GAME_MODES,
    GENRES,
    ID,
    INVOLVED_COMPANIES,
    MULTIPLAYER_MODE,
    NAME,
    PARENT_GAME,
    PLATFORMS,
    RELEASE_DATE,
    REMAKES,
    SCREENSHOTS,
    SIMILAR_GAMES,
    SUMMARY,
    VIDEOS,
    URL,
    WEBSITES;

    val fieldName: String
        get() = name.lowercase()
}

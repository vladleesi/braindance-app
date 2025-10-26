package dev.vladleesi.braindanceapp.utils

import dev.vladleesi.braindanceapp.data.models.games.RatingCategory

private const val AGE_RATINGS_RATING = "age_ratings.rating_category"

fun excludeAdultOnlyGames() =
    "age_ratings != null\n" +
        "  & $AGE_RATINGS_RATING != ${RatingCategory.AO.id}\n" +
        "  & $AGE_RATINGS_RATING != ${RatingCategory.GRAC_19_PLUS.id}\n" +
        "  & $AGE_RATINGS_RATING != ${RatingCategory.ACB_R18_PLUS.id}\n"

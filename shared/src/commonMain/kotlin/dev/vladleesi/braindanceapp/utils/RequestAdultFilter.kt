package dev.vladleesi.braindanceapp.utils

import dev.vladleesi.braindanceapp.data.models.games.Rating

private const val AGE_RATINGS_RATING = "age_ratings.rating"

fun excludeAdultOnlyGames() =
    "age_ratings != null\n" +
        "  & $AGE_RATINGS_RATING != ${Rating.AO.value}\n" +
        "  & $AGE_RATINGS_RATING != ${Rating.CERO_Z.value}\n" +
        "  & $AGE_RATINGS_RATING != ${Rating.USK_18.value}\n" +
        "  & $AGE_RATINGS_RATING != ${Rating.GRAC_EIGHTEEN.value}\n" +
        "  & $AGE_RATINGS_RATING != ${Rating.CLASS_IND_EIGHTEEN.value}\n" +
        "  & $AGE_RATINGS_RATING != ${Rating.ACB_R18.value}"

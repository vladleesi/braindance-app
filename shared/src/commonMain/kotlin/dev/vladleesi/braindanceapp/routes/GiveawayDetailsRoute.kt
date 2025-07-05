package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.savedstate.SavedState
import dev.vladleesi.braindanceapp.ui.screens.GiveawayDetailsScreen

data object GiveawayDetailsRoute : Route() {
    override val name
        get() = "${super.name}/{${Params.GIVEAWAY_ID}}"

    @Composable
    override fun renderContent(
        savedState: SavedState?,
        navHostController: NavHostController?,
    ) {
        GiveawayDetailsScreen(savedState, navHostController)
    }

    object Params {
        const val GIVEAWAY_ID = "giveawayId"
    }
}

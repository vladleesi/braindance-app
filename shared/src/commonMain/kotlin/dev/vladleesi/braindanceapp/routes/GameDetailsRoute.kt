package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.savedstate.SavedState
import dev.vladleesi.braindanceapp.ui.screens.GameDetailsScreen

data object GameDetailsRoute : Route() {
    override val name
        get() = "${super.name}/{${Params.GAME_ID}}"

    @Composable
    override fun Content(
        savedState: SavedState?,
        navHostController: NavHostController?,
    ) {
        GameDetailsScreen(savedState, navHostController)
    }

    object Params {
        const val GAME_ID = "gameId"
    }
}

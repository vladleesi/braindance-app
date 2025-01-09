package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.core.bundle.Bundle
import androidx.navigation.NavHostController
import dev.vladleesi.braindanceapp.ui.screens.GameDetailsScreen

data object GameDetailsRoute : Route() {
    override val name
        get() = "${super.name}/{${Params.GAME_ID}}"

    @Composable
    override fun renderContent(
        bundle: Bundle?,
        navHostController: NavHostController?,
    ) {
        GameDetailsScreen(bundle, navHostController)
    }

    object Params {
        const val GAME_ID = "gameId"
    }
}

package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.savedstate.SavedState
import dev.vladleesi.braindanceapp.ui.screens.HomeScreen

data object HomeRoute : Route() {
    @Composable
    override fun renderContent(
        savedState: SavedState?,
        navHostController: NavHostController?,
    ) {
        HomeScreen()
    }
}

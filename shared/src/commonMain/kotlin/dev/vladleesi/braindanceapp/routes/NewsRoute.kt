package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.savedstate.SavedState
import dev.vladleesi.braindanceapp.ui.screens.NewsScreen

data object NewsRoute : Route() {
    @Composable
    override fun Content(
        savedState: SavedState?,
        navHostController: NavHostController?,
    ) {
        NewsScreen()
    }
}

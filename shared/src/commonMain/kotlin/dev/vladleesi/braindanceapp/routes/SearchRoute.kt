package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.core.bundle.Bundle
import androidx.navigation.NavHostController
import dev.vladleesi.braindanceapp.ui.screens.SearchScreen

data object SearchRoute : Route() {
    const val SEARCH_TAB_RESELECTED = "searchTabReselected"

    @Composable
    override fun renderContent(
        bundle: Bundle?,
        navHostController: NavHostController?,
    ) {
        SearchScreen(navHostController)
    }
}

package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.core.bundle.Bundle
import androidx.navigation.NavHostController
import dev.vladleesi.braindanceapp.ui.screens.CalendarScreen

data object CalendarRoute : Route() {
    @Composable
    override fun renderContent(
        bundle: Bundle?,
        navHostController: NavHostController?,
    ) {
        CalendarScreen()
    }
}

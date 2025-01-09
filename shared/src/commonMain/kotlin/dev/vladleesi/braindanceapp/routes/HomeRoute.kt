package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.core.bundle.Bundle
import dev.vladleesi.braindanceapp.ui.screens.HomeScreen

data object HomeRoute : Route() {
    @Composable
    override fun renderContent(bundle: Bundle?) {
        HomeScreen()
    }
}

package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import dev.vladleesi.braindanceapp.ui.screens.GameDetailsScreen
import kotlinx.serialization.Serializable

@Serializable
data class GameDetailsRoute(
    val id: Int,
) : Route {
    @Composable
    override fun Content(navHostController: NavHostController?) {
        GameDetailsScreen(id, navHostController)
    }
}

package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import dev.vladleesi.braindanceapp.ui.screens.GiveawayDetailsScreen
import kotlinx.serialization.Serializable

@Serializable
data class GiveawayDetailsRoute(
    val id: Int,
) : Route {
    @Composable
    override fun Content(navHostController: NavHostController?) {
        GiveawayDetailsScreen(this, navHostController)
    }
}

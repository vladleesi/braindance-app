package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import dev.vladleesi.braindanceapp.ui.screens.FeedScreen
import kotlinx.serialization.Serializable

@Serializable
data object FeedRoute : Route {
    @Composable
    override fun Content(navHostController: NavHostController?) {
        if (navHostController != null) {
            FeedScreen(modifier = Modifier, navHostController = navHostController)
        }
    }
}

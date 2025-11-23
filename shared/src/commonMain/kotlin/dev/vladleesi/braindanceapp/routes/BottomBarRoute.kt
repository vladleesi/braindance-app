package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.vladleesi.braindanceapp.ui.screens.CollectionsScreen
import dev.vladleesi.braindanceapp.ui.screens.NewsScreen
import dev.vladleesi.braindanceapp.ui.screens.ProfileScreen
import dev.vladleesi.braindanceapp.ui.screens.SearchScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class BottomBarRoute : Route {
    @Serializable
    object HomeRoute : BottomBarRoute() {
        @Composable
        override fun Content(navHostController: NavHostController?) {
            val childNavController = rememberNavController()
            NavHost(navController = childNavController, startDestination = FeedRoute) {
                registerInnerRoute<FeedRoute>(navHostController = childNavController)
                registerInnerRoute<GameDetailsRoute>(navHostController = childNavController)
                registerInnerRoute<GiveawayDetailsRoute>(navHostController = childNavController)
            }
        }
    }

    @Serializable
    object NewsRoute : BottomBarRoute() {
        @Composable
        override fun Content(navHostController: NavHostController?) {
            NewsScreen()
        }
    }

    @Serializable
    object SearchRoute : BottomBarRoute() {
        const val SEARCH_TAB_RESELECTED = "searchTabReselected"

        @Composable
        override fun Content(navHostController: NavHostController?) {
            SearchScreen(navHostController)
        }
    }

    @Serializable
    object CollectionsRoute : BottomBarRoute() {
        @Composable
        override fun Content(navHostController: NavHostController?) {
            CollectionsScreen()
        }
    }

    @Serializable
    object ProfileRoute : BottomBarRoute() {
        @Composable
        override fun Content(navHostController: NavHostController?) {
            ProfileScreen()
        }
    }
}

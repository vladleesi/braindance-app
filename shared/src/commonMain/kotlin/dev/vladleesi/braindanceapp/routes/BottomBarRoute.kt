package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import dev.vladleesi.braindanceapp.ui.screens.CollectionsScreen
import dev.vladleesi.braindanceapp.ui.screens.HomeScreen
import dev.vladleesi.braindanceapp.ui.screens.NewsScreen
import dev.vladleesi.braindanceapp.ui.screens.ProfileScreen
import dev.vladleesi.braindanceapp.ui.screens.SearchScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class BottomBarRoute(
    val route: String,
) : Route {
    @Serializable
    object HomeRoute : BottomBarRoute("HomeRoute") {
        @Composable
        override fun Content(navHostController: NavHostController?) {
            HomeScreen()
        }
    }

    @Serializable
    object NewsRoute : BottomBarRoute("NewsRoute") {
        @Composable
        override fun Content(navHostController: NavHostController?) {
            NewsScreen()
        }
    }

    @Serializable
    object SearchRoute : BottomBarRoute("SearchRoute") {
        const val SEARCH_TAB_RESELECTED = "searchTabReselected"

        @Composable
        override fun Content(navHostController: NavHostController?) {
            SearchScreen(navHostController)
        }
    }

    @Serializable
    object CollectionsRoute : BottomBarRoute("CollectionsRoute") {
        @Composable
        override fun Content(navHostController: NavHostController?) {
            CollectionsScreen()
        }
    }

    @Serializable
    object ProfileRoute : BottomBarRoute("ProfileRoute") {
        @Composable
        override fun Content(navHostController: NavHostController?) {
            ProfileScreen()
        }
    }

    companion object {
        fun fromRoute(route: String?): BottomBarRoute? =
            when {
                route.isNullOrEmpty() -> null
                route.contains(HomeRoute.route, ignoreCase = true) -> HomeRoute
                route.contains(NewsRoute.route, ignoreCase = true) -> NewsRoute
                route.contains(SearchRoute.route, ignoreCase = true) -> SearchRoute
                route.contains(CollectionsRoute.route, ignoreCase = true) -> CollectionsRoute
                route.contains(ProfileRoute.route, ignoreCase = true) -> ProfileRoute
                else -> null
            }
    }
}

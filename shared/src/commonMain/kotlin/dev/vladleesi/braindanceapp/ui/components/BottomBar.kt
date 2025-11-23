package dev.vladleesi.braindanceapp.ui.components

import Album
import CircleUserRound
import House
import Newspaper
import Search
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.vladleesi.braindanceapp.routes.BottomBarRoute
import dev.vladleesi.braindanceapp.routes.BottomBarRoute.CollectionsRoute.currentBottomBarRoute
import dev.vladleesi.braindanceapp.routes.Route
import dev.vladleesi.braindanceapp.routes.registerMainRoute
import dev.vladleesi.braindanceapp.ui.style.navBar
import dev.vladleesi.braindanceapp.ui.style.secondaryText
import dev.vladleesi.braindanceapp.ui.style.white

@Composable
fun BottomBar(navController: NavHostController) {
    BottomNavigation(
        backgroundColor = navBar,
        modifier = Modifier.navigationBarsPadding(),
        elevation = 0.dp,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute: BottomBarRoute? = navBackStackEntry?.destination?.currentBottomBarRoute

        BottomBarItem.entries.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.imageVector,
                        contentDescription = null,
                    )
                },
                selected = currentRoute == item.route,
                selectedContentColor = white,
                unselectedContentColor = secondaryText,
                onClick = { navController.navigateToRoute(item.route, currentRoute) },
            )
        }
    }
}

@Composable
fun NavigationGraph(
    modifier: Modifier,
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = BottomBarRoute.HomeRoute, modifier) {
        registerMainRoute<BottomBarRoute.HomeRoute>()
        registerMainRoute<BottomBarRoute.NewsRoute>()
        registerMainRoute<BottomBarRoute.SearchRoute>()
        registerMainRoute<BottomBarRoute.CollectionsRoute>()
        registerMainRoute<BottomBarRoute.ProfileRoute>()
    }
}

private fun NavHostController.navigateToRoute(
    target: Route,
    currentRoute: Route?,
) {
    if (currentRoute == target && currentRoute == BottomBarRoute.SearchRoute) {
        // Handle second click on search item
        currentBackStackEntry?.savedStateHandle?.set(
            BottomBarRoute.SearchRoute.SEARCH_TAB_RESELECTED,
            true,
        )
        return // Prevent unnecessary navigation
    }

    navigate(target) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destination
        // on the back stack as users select items
        popUpTo<BottomBarRoute.HomeRoute> {
            saveState = true
        }
        // Avoid multiple copies of the same destination
        // when reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

enum class BottomBarItem(
    val imageVector: ImageVector,
    val route: Route,
) {
    HomeItem(imageVector = House, route = BottomBarRoute.HomeRoute),
    NewsItem(imageVector = Newspaper, route = BottomBarRoute.NewsRoute),
    SearchItem(imageVector = Search, route = BottomBarRoute.SearchRoute),
    CollectionsItem(imageVector = Album, route = BottomBarRoute.CollectionsRoute),
    ProfileItem(imageVector = CircleUserRound, route = BottomBarRoute.ProfileRoute),
}

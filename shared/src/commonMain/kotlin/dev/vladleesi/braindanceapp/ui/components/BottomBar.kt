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
import dev.vladleesi.braindanceapp.routes.CollectionsRoute
import dev.vladleesi.braindanceapp.routes.HomeRoute
import dev.vladleesi.braindanceapp.routes.NewsRoute
import dev.vladleesi.braindanceapp.routes.ProfileRoute
import dev.vladleesi.braindanceapp.routes.SearchRoute
import dev.vladleesi.braindanceapp.routes.registerRoute
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
        val currentRoute = navBackStackEntry?.destination?.route
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
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destination
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(HomeRoute.name) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination
                        // when reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = HomeRoute.name) {
        registerRoute(HomeRoute)
        registerRoute(NewsRoute)
        registerRoute(SearchRoute)
        registerRoute(CollectionsRoute)
        registerRoute(ProfileRoute)
    }
}

enum class BottomBarItem(
    val imageVector: ImageVector,
    val route: String,
) {
    HomeItem(imageVector = House, route = HomeRoute.name),
    NewsItem(imageVector = Newspaper, route = NewsRoute.name),
    SearchItem(imageVector = Search, route = SearchRoute.name),
    CollectionsItem(imageVector = Album, route = CollectionsRoute.name),
    ProfileItem(imageVector = CircleUserRound, route = ProfileRoute.name),
}

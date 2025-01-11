package dev.vladleesi.braindanceapp.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.vladleesi.braindanceapp.resources.Res
import dev.vladleesi.braindanceapp.resources.ic_calendar
import dev.vladleesi.braindanceapp.resources.ic_collections
import dev.vladleesi.braindanceapp.resources.ic_home
import dev.vladleesi.braindanceapp.resources.ic_news
import dev.vladleesi.braindanceapp.resources.ic_profile
import dev.vladleesi.braindanceapp.routes.CalendarRoute
import dev.vladleesi.braindanceapp.routes.CollectionsRoute
import dev.vladleesi.braindanceapp.routes.HomeRoute
import dev.vladleesi.braindanceapp.routes.NewsRoute
import dev.vladleesi.braindanceapp.routes.ProfileRoute
import dev.vladleesi.braindanceapp.routes.registerRoute
import dev.vladleesi.braindanceapp.ui.style.navBarColor
import dev.vladleesi.braindanceapp.ui.style.secondaryText
import dev.vladleesi.braindanceapp.ui.style.white
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomBar(navController: NavHostController) {
    BottomNavigation(backgroundColor = navBarColor, modifier = Modifier.navigationBarsPadding()) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        BottomBarItem.entries.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(resource = item.icon),
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
        registerRoute(CalendarRoute)
        registerRoute(CollectionsRoute)
        registerRoute(ProfileRoute)
    }
}

enum class BottomBarItem(
    val icon: DrawableResource,
    val route: String,
) {
    Home(icon = Res.drawable.ic_home, route = HomeRoute.name),
    News(icon = Res.drawable.ic_news, route = NewsRoute.name),
    Calendar(icon = Res.drawable.ic_calendar, route = CalendarRoute.name),
    Collections(icon = Res.drawable.ic_collections, route = CollectionsRoute.name),
    Profile(icon = Res.drawable.ic_profile, route = ProfileRoute.name),
}

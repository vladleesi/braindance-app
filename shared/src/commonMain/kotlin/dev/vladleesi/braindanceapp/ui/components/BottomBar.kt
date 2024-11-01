package dev.vladleesi.braindanceapp.ui.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.vladleesi.braindanceapp.resources.Res
import dev.vladleesi.braindanceapp.resources.bottom_bar_calendar
import dev.vladleesi.braindanceapp.resources.bottom_bar_collections
import dev.vladleesi.braindanceapp.resources.bottom_bar_home
import dev.vladleesi.braindanceapp.resources.bottom_bar_news
import dev.vladleesi.braindanceapp.resources.bottom_bar_settings
import dev.vladleesi.braindanceapp.resources.ic_calendar
import dev.vladleesi.braindanceapp.resources.ic_collections
import dev.vladleesi.braindanceapp.resources.ic_home
import dev.vladleesi.braindanceapp.resources.ic_news
import dev.vladleesi.braindanceapp.resources.ic_settings
import dev.vladleesi.braindanceapp.ui.screens.CalendarScreen
import dev.vladleesi.braindanceapp.ui.screens.CollectionsScreen
import dev.vladleesi.braindanceapp.ui.screens.HomeScreen
import dev.vladleesi.braindanceapp.ui.screens.NewsScreen
import dev.vladleesi.braindanceapp.ui.screens.ProfileScreen
import dev.vladleesi.braindanceapp.ui.style.background
import dev.vladleesi.braindanceapp.ui.style.secondary_text
import dev.vladleesi.braindanceapp.ui.style.white
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomBar(navController: NavHostController) {
    BottomNavigation(backgroundColor = background) {
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
                selected = currentRoute == item.name,
                selectedContentColor = white,
                unselectedContentColor = secondary_text,
                onClick = {
                    navController.navigate(item.name) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destination
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(BottomBarItem.HOME.name) {
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
    NavHost(navController = navController, startDestination = BottomBarItem.HOME.name) {
        composable(BottomBarItem.HOME.name) { HomeScreen() }
        composable(BottomBarItem.NEWS.name) { NewsScreen() }
        composable(BottomBarItem.CALENDAR.name) { CalendarScreen() }
        composable(BottomBarItem.COLLECTIONS.name) { CollectionsScreen() }
        composable(BottomBarItem.SETTINGS.name) { ProfileScreen() }
    }
}

enum class BottomBarItem(
    val title: StringResource,
    val icon: DrawableResource,
) {
    HOME(
        title = Res.string.bottom_bar_home,
        icon = Res.drawable.ic_home,
    ),
    NEWS(
        title = Res.string.bottom_bar_news,
        icon = Res.drawable.ic_news,
    ),
    CALENDAR(
        title = Res.string.bottom_bar_calendar,
        icon = Res.drawable.ic_calendar,
    ),
    COLLECTIONS(
        title = Res.string.bottom_bar_collections,
        icon = Res.drawable.ic_collections,
    ),
    SETTINGS(
        title = Res.string.bottom_bar_settings,
        icon = Res.drawable.ic_settings,
    ),
}

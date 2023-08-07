package dev.vladleesi.braindanceapp.android.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.vladleesi.braindanceapp.android.R
import dev.vladleesi.braindanceapp.android.screens.HomeScreen
import dev.vladleesi.braindanceapp.presentation.screens.CalendarScreen
import dev.vladleesi.braindanceapp.presentation.screens.CollectionsScreen
import dev.vladleesi.braindanceapp.presentation.screens.NewsScreen
import dev.vladleesi.braindanceapp.presentation.screens.SettingsScreen
import dev.vladleesi.braindanceapp.presentation.style.background
import dev.vladleesi.braindanceapp.presentation.style.secondary_text
import dev.vladleesi.braindanceapp.presentation.style.white

@Composable
fun BottomBar(navController: NavHostController, updateLabel: (Int) -> Unit) {
    // TODO: Find the navigation for Compose Multiplatform
    BottomNavigation(backgroundColor = background) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        updateLabel.invoke(
            when (currentRoute) {
                BottomBarItem.NEWS.name -> BottomBarItem.NEWS.title
                BottomBarItem.CALENDAR.name -> BottomBarItem.CALENDAR.title
                BottomBarItem.COLLECTIONS.name -> BottomBarItem.COLLECTIONS.title
                BottomBarItem.SETTINGS.name -> BottomBarItem.SETTINGS.title
                else -> BottomBarItem.HOME.title
            }
        )
        BottomBarItem.values().forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = null
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
                        popUpTo(navController.graph.startDestinationId)
                        // Avoid multiple copies of the same destination
                        // when reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
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
        composable(BottomBarItem.SETTINGS.name) { SettingsScreen() }
    }
}

enum class BottomBarItem(
    @StringRes val title: Int,
    @DrawableRes val icon: Int
) {
    HOME(
        title = R.string.bottom_bar_home,
        icon = R.drawable.ic_home
    ),
    NEWS(
        title = R.string.bottom_bar_news,
        icon = R.drawable.ic_news
    ),
    CALENDAR(
        title = R.string.bottom_bar_calendar,
        icon = R.drawable.ic_calendar
    ),
    COLLECTIONS(
        title = R.string.bottom_bar_collections,
        icon = R.drawable.ic_collections
    ),
    SETTINGS(
        title = R.string.bottom_bar_settings,
        icon = R.drawable.ic_settings
    )
}

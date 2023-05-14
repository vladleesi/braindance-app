package io.github.vladleesi.braindanceapp.android.components

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
import io.github.vladleesi.braindanceapp.android.R
import io.github.vladleesi.braindanceapp.android.screens.CollectionsScreen
import io.github.vladleesi.braindanceapp.android.screens.CommunityScreen
import io.github.vladleesi.braindanceapp.android.screens.HomeScreen
import io.github.vladleesi.braindanceapp.android.screens.ProfileScreen
import io.github.vladleesi.braindanceapp.android.style.background
import io.github.vladleesi.braindanceapp.android.style.secondary_text
import io.github.vladleesi.braindanceapp.android.style.white


@Composable
fun BottomBar(navController: NavHostController, updateLabel: (Int) -> Unit) {
    BottomNavigation(backgroundColor = background) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        updateLabel.invoke(
            when (currentRoute) {
                BottomBarItem.COLLECTIONS.name -> BottomBarItem.COLLECTIONS.title
                BottomBarItem.COMMUNITY.name -> BottomBarItem.COMMUNITY.title
                BottomBarItem.PROFILE.name -> BottomBarItem.PROFILE.title
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
        composable(BottomBarItem.COLLECTIONS.name) { CollectionsScreen() }
        composable(BottomBarItem.COMMUNITY.name) { CommunityScreen() }
        composable(BottomBarItem.PROFILE.name) { ProfileScreen() }
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
    COLLECTIONS(
        title = R.string.bottom_bar_collections,
        icon = R.drawable.ic_collections
    ),
    COMMUNITY(
        title = R.string.bottom_bar_community,
        icon = R.drawable.ic_community
    ),
    PROFILE(
        title = R.string.bottom_bar_profile,
        icon = R.drawable.ic_user
    )
}

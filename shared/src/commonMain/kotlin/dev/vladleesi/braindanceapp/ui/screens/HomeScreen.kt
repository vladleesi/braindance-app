package dev.vladleesi.braindanceapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.vladleesi.braindanceapp.resources.Res
import dev.vladleesi.braindanceapp.resources.home_screen_all_time_top_title
import dev.vladleesi.braindanceapp.resources.home_screen_popular_in_year_title
import dev.vladleesi.braindanceapp.resources.home_screen_popular_this_year_title
import dev.vladleesi.braindanceapp.resources.home_screen_this_week_release_title
import dev.vladleesi.braindanceapp.routes.GameDetailsRoute
import dev.vladleesi.braindanceapp.routes.registerRoute
import dev.vladleesi.braindanceapp.ui.components.MiniGameCardList
import dev.vladleesi.braindanceapp.ui.components.SpacerStatusBarInsets
import dev.vladleesi.braindanceapp.ui.components.StatusBarOverlay
import dev.vladleesi.braindanceapp.ui.style.background
import dev.vladleesi.braindanceapp.ui.style.large
import dev.vladleesi.braindanceapp.ui.viewmodels.HomeViewModel
import dev.vladleesi.braindanceapp.utils.lastYear
import org.jetbrains.compose.resources.stringResource

private const val HOME_INNER_ROUTE = "HomeInnerRoute"

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val childNavController = rememberNavController()
    NavHost(navController = childNavController, startDestination = HOME_INNER_ROUTE) {
        composable(HOME_INNER_ROUTE) {
            HomeScreen(modifier = modifier, navHostController = childNavController)
        }
        registerRoute(route = GameDetailsRoute, navHostController = childNavController)
    }
}

@Composable
private fun HomeScreen(
    viewModel: HomeViewModel = viewModel { HomeViewModel() },
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    var isInitialized by rememberSaveable { mutableStateOf(false) }

    val popularThisYear by viewModel.popularThisYear.collectAsState()
    val popularLastYear by viewModel.popularLastYear.collectAsState()
    val allTimeTop by viewModel.allTimeTop.collectAsState()
    val thisWeekReleases by viewModel.thisWeekReleases.collectAsState()

    // Load the screen only on first navigation
    if (isInitialized.not()) {
        LaunchedEffect(Unit) {
            isInitialized = true
            viewModel.loadHome()
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(background),
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(large),
        ) {
            item { SpacerStatusBarInsets() }
            item {
                MiniGameCardList(
                    title = stringResource(Res.string.home_screen_popular_this_year_title),
                    state = popularThisYear,
                    navHostController = navHostController,
                )
            }
            item {
                MiniGameCardList(
                    title = stringResource(Res.string.home_screen_popular_in_year_title, lastYear),
                    state = popularLastYear,
                    navHostController = navHostController,
                )
            }
            item {
                // TODO: Two lines with auto scroll
                MiniGameCardList(
                    title = stringResource(Res.string.home_screen_all_time_top_title),
                    state = allTimeTop,
                    navHostController = navHostController,
                )
            }
            item {
                MiniGameCardList(
                    title = stringResource(Res.string.home_screen_this_week_release_title),
                    state = thisWeekReleases,
                    navHostController = navHostController,
                )
            }
            item { Spacer(Modifier.height(large)) }
        }
        StatusBarOverlay()
    }
}

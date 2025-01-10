package dev.vladleesi.braindanceapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import dev.vladleesi.braindanceapp.routes.GameDetailsRoute
import dev.vladleesi.braindanceapp.routes.registerRoute
import dev.vladleesi.braindanceapp.ui.components.MiniGameCardList
import dev.vladleesi.braindanceapp.ui.style.large
import dev.vladleesi.braindanceapp.ui.style.medium
import dev.vladleesi.braindanceapp.ui.viewmodels.HomeViewModel
import dev.vladleesi.braindanceapp.utils.lastYear

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

    val bestOfTheYear by viewModel.bestOfTheYear.collectAsState()
    val popularLastYear by viewModel.popularLastYear.collectAsState()
    val allTimeTop by viewModel.allTimeTop.collectAsState()

    if (isInitialized.not()) {
        LaunchedEffect(Unit) {
            isInitialized = true
            // Load the screen only on first navigation
            viewModel.load()
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(large),
    ) {
        Spacer(Modifier.height(medium))
        MiniGameCardList(
            // TODO: Move to res
            title = "Best of the year",
            cardList = bestOfTheYear,
            navHostController = navHostController,
        )
        MiniGameCardList(
            title = "Popular in $lastYear",
            cardList = popularLastYear,
            navHostController = navHostController,
        )
        // TODO: Two lines with auto scroll
        MiniGameCardList(
            title = "All time top 250",
            cardList = allTimeTop,
            navHostController = navHostController,
        )
        Spacer(Modifier.height(large))
    }
}

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
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.vladleesi.braindanceapp.resources.Res
import dev.vladleesi.braindanceapp.resources.home_screen_popular_latest_giveaways
import dev.vladleesi.braindanceapp.resources.home_screen_popular_most_anticipated
import dev.vladleesi.braindanceapp.resources.home_screen_popular_popular_right_now
import dev.vladleesi.braindanceapp.routes.GameDetailsRoute
import dev.vladleesi.braindanceapp.routes.GiveawayDetailsRoute
import dev.vladleesi.braindanceapp.routes.registerRoute
import dev.vladleesi.braindanceapp.ui.components.SpacerStatusBarInsets
import dev.vladleesi.braindanceapp.ui.components.StatusBarOverlay
import dev.vladleesi.braindanceapp.ui.components.giveaways.GiveawaysList
import dev.vladleesi.braindanceapp.ui.components.home.MiniGameCardList
import dev.vladleesi.braindanceapp.ui.style.Dimens
import dev.vladleesi.braindanceapp.ui.style.background
import dev.vladleesi.braindanceapp.ui.style.overlayBlackWith80Alpha
import dev.vladleesi.braindanceapp.ui.viewmodels.HomeViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val HOME_INNER_ROUTE = "HomeInnerRoute"

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val childNavController = rememberNavController()
    NavHost(navController = childNavController, startDestination = HOME_INNER_ROUTE) {
        composable(HOME_INNER_ROUTE) {
            HomeScreen(modifier = modifier, navHostController = childNavController)
        }
        registerRoute(route = GameDetailsRoute, navHostController = childNavController)
        registerRoute(route = GiveawayDetailsRoute, navHostController = childNavController)
    }
}

@Composable
private fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    val mostAnticipated by viewModel.mostAnticipated.collectAsState()
    val giveaways by viewModel.giveaways.collectAsState()
    val popularRightNow by viewModel.popularRightNow.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    // Load the screen only on first navigation
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
            verticalArrangement = Arrangement.spacedBy(Dimens.large),
        ) {
            item { SpacerStatusBarInsets() }
            item {
                MiniGameCardList(
                    title = stringResource(Res.string.home_screen_popular_most_anticipated),
                    state = mostAnticipated,
                    navHostController = navHostController,
                )
            }
            item {
                GiveawaysList(
                    title = stringResource(Res.string.home_screen_popular_latest_giveaways),
                    state = giveaways,
                    navHostController = navHostController,
                )
            }
            item {
                MiniGameCardList(
                    title = stringResource(Res.string.home_screen_popular_popular_right_now),
                    state = popularRightNow,
                    navHostController = navHostController,
                )
            }
            item { Spacer(Modifier.height(Dimens.large)) }
        }
        StatusBarOverlay(backgroundColor = overlayBlackWith80Alpha)
    }
}

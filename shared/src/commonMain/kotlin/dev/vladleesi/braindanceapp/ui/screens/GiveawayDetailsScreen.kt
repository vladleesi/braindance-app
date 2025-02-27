package dev.vladleesi.braindanceapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import androidx.core.bundle.Bundle
import androidx.navigation.NavHostController
import dev.vladleesi.braindanceapp.routes.GiveawayDetailsRoute
import dev.vladleesi.braindanceapp.system.isLargeDevice
import dev.vladleesi.braindanceapp.ui.components.GlobalLoading
import dev.vladleesi.braindanceapp.ui.components.SpacerTopBarWithStatusBarInsets
import dev.vladleesi.braindanceapp.ui.components.StatusBarOverlay
import dev.vladleesi.braindanceapp.ui.components.TopAppBar
import dev.vladleesi.braindanceapp.ui.style.background
import dev.vladleesi.braindanceapp.ui.style.secondaryVariant
import dev.vladleesi.braindanceapp.ui.viewmodels.GiveawayDetailsState
import dev.vladleesi.braindanceapp.ui.viewmodels.GiveawayDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GiveawayDetailsScreen(
    arguments: Bundle?,
    navHostController: NavHostController?,
    modifier: Modifier = Modifier,
    viewModel: GiveawayDetailsViewModel = koinViewModel(),
) {
    var isInitialized by rememberSaveable { mutableStateOf(false) }

    val giveawayId by rememberSaveable {
        mutableStateOf(arguments?.getString(GiveawayDetailsRoute.Params.GIVEAWAY_ID)?.toIntOrNull())
    }
    val state by viewModel.giveawayDetailsState.collectAsState()

    if (giveawayId != null && isInitialized.not()) {
        LaunchedEffect(Unit) {
            viewModel.loadGiveawayDetails(giveawayId)
            isInitialized = true
        }
    }

    when (val currentState = state) {
        is GiveawayDetailsState.Error ->
            // TODO: Add back button
            ErrorScreen(errorMessage = currentState.message, modifier = modifier) {
                viewModel.loadGiveawayDetails(giveawayId)
            }

        GiveawayDetailsState.Loading ->
            GlobalLoading(modifier = modifier)

        is GiveawayDetailsState.Success ->
            GiveawayDetailsScreen(state = currentState, modifier = modifier, navHostController = navHostController)
    }
}

@Composable
fun GiveawayDetailsScreen(
    state: GiveawayDetailsState.Success,
    modifier: Modifier,
    navHostController: NavHostController?,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(background),
    ) {
        val isLargeDevice = isLargeDevice()

        // TODO: Rework (wip)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item { SpacerTopBarWithStatusBarInsets() }
            item { Text(text = state.giveawayDetails.title.orEmpty()) }
            item { OpenGiveawayButton(url = state.giveawayDetails.openGiveawayUrl.orEmpty()) }
        }
        Column {
            StatusBarOverlay()
            TopAppBar(
                showBackButton = true,
                onBackButtonPressed = { navHostController?.popBackStack() },
            )
        }
    }
}

@Composable
private fun OpenGiveawayButton(
    url: String,
    modifier: Modifier = Modifier,
    uriHandler: UriHandler = LocalUriHandler.current,
) {
    Button(
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = secondaryVariant),
        onClick = { uriHandler.openUri(url) },
    ) {
        Text(text = "Open giveaway url")
    }
}

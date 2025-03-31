package dev.vladleesi.braindanceapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.bundle.Bundle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import dev.vladleesi.braindanceapp.routes.GiveawayDetailsRoute
import dev.vladleesi.braindanceapp.system.isLargeDevice
import dev.vladleesi.braindanceapp.ui.components.GlobalLoading
import dev.vladleesi.braindanceapp.ui.components.SpacerTopBarWithStatusBarInsets
import dev.vladleesi.braindanceapp.ui.components.StatusBarOverlay
import dev.vladleesi.braindanceapp.ui.components.TopAppBar
import dev.vladleesi.braindanceapp.ui.style.Dimens
import dev.vladleesi.braindanceapp.ui.style.background
import dev.vladleesi.braindanceapp.ui.style.icons.OpenInNew
import dev.vladleesi.braindanceapp.ui.style.secondaryText
import dev.vladleesi.braindanceapp.ui.style.secondaryVariant
import dev.vladleesi.braindanceapp.ui.style.white
import dev.vladleesi.braindanceapp.ui.viewmodels.GiveawayDetailsState
import dev.vladleesi.braindanceapp.ui.viewmodels.GiveawayDetailsViewModel
import dev.vladleesi.braindanceapp.utils.toContentDescription
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

    if (giveawayId != null && !isInitialized) {
        LaunchedEffect(Unit) {
            viewModel.loadGiveawayDetails(giveawayId)
            isInitialized = true
        }
    }

    when (val currentState = state) {
        is GiveawayDetailsState.Error ->
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

        // TODO: Rework (wip: claimed, large devices, timer, platforms, new ui model data class)
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.medium)) {
            item { SpacerTopBarWithStatusBarInsets() }
            item {
                Box(modifier = Modifier.padding(top = Dimens.medium)) {
                    AsyncImage(
                        modifier =
                            Modifier
                                .height(Dimens.giveAwayCardHeight)
                                .width(Dimens.giveAwayCardWidth)
                                .clip(RoundedCornerShape(Dimens.small)),
                        model = state.giveawayDetails.image,
                        contentScale = ContentScale.Crop,
                        contentDescription = state.giveawayDetails.title?.toContentDescription(),
                    )
                }
            }
            item { Spacer(Modifier.height(Dimens.small)) }
            item {
                Text(
                    text = state.giveawayDetails.title.orEmpty(),
                    style = MaterialTheme.typography.h2,
                )
            }
            item { Spacer(Modifier.height(Dimens.small)) }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "FREE",
                        color = white,
                        style = MaterialTheme.typography.body2,
                    )
                    Spacer(modifier = Modifier.width(Dimens.tiny))
                    Text(
                        text = state.giveawayDetails.worth.orEmpty(),
                        style = MaterialTheme.typography.body2,
                        color = secondaryText,
                        textDecoration = TextDecoration.LineThrough,
                    )
                }
            }
            item { Spacer(Modifier.height(Dimens.small)) }
            // TODO: Move these two items to one composable
            item {
                Text(
                    // TODO: Move to res
                    text = "About this giveaway",
                    style = MaterialTheme.typography.h3,
                )
            }
            item { Spacer(Modifier.height(Dimens.micro)) }
            item {
                Text(
                    text = state.giveawayDetails.description.orEmpty(),
                    style = MaterialTheme.typography.body2,
                )
            }
            item { Spacer(Modifier.height(Dimens.small)) }
            item {
                Text(
                    text = "Instruction",
                    style = MaterialTheme.typography.h3,
                )
            }
            item { Spacer(Modifier.height(Dimens.micro)) }
            item {
                Text(
                    text = state.giveawayDetails.instructions.orEmpty(),
                    style = MaterialTheme.typography.body2,
                )
            }
            item { Spacer(Modifier.height(Dimens.medium)) }
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
        Row {
            Text(
                text = "Get Giveaway",
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            Spacer(Modifier.width(Dimens.tiny))
            Icon(
                imageVector = OpenInNew,
                contentDescription = "Share",
                tint = white,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
    }
}

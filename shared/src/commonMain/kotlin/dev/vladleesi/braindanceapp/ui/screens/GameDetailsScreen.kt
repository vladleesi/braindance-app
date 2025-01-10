package dev.vladleesi.braindanceapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.bundle.Bundle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import dev.vladleesi.braindanceapp.resources.Res
import dev.vladleesi.braindanceapp.resources.back_button_content_description
import dev.vladleesi.braindanceapp.resources.ic_arrow_back
import dev.vladleesi.braindanceapp.routes.GameDetailsRoute
import dev.vladleesi.braindanceapp.system.screenSize
import dev.vladleesi.braindanceapp.ui.components.ExpandableText
import dev.vladleesi.braindanceapp.ui.components.GlobalLoading
import dev.vladleesi.braindanceapp.ui.components.PlatformLogoList
import dev.vladleesi.braindanceapp.ui.components.ReleaseDateLabel
import dev.vladleesi.braindanceapp.ui.style.getTypography
import dev.vladleesi.braindanceapp.ui.style.large
import dev.vladleesi.braindanceapp.ui.style.medium
import dev.vladleesi.braindanceapp.ui.style.small
import dev.vladleesi.braindanceapp.ui.style.tiny
import dev.vladleesi.braindanceapp.ui.style.white
import dev.vladleesi.braindanceapp.ui.viewmodels.GameDetailsState
import dev.vladleesi.braindanceapp.ui.viewmodels.GameDetailsViewModel
import dev.vladleesi.braindanceapp.utils.toContentDescription
import dev.vladleesi.braindanceapp.utils.withCircleRippleEffect
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

private const val IMAGE_HEIGHT_FACTOR = 0.6f

@Composable
fun GameDetailsScreen(
    arguments: Bundle?,
    navHostController: NavHostController?,
    modifier: Modifier = Modifier,
    viewModel: GameDetailsViewModel = viewModel { GameDetailsViewModel() },
) {
    var isInitialized by rememberSaveable { mutableStateOf(false) }

    val gameId by rememberSaveable {
        mutableStateOf(arguments?.getString(GameDetailsRoute.Params.GAME_ID))
    }
    val state by viewModel.gameDetailsState.collectAsState()

    if (gameId != null && isInitialized.not()) {
        LaunchedEffect(Unit) {
            viewModel.loadGameDetails(gameId)
            isInitialized = true
        }
    }

    when (val currentState = state) {
        is GameDetailsState.Error ->
            ErrorScreen(errorMessage = currentState.message, modifier = modifier) {
                viewModel.loadGameDetails(gameId)
            }

        GameDetailsState.Loading ->
            GlobalLoading(modifier = modifier)

        is GameDetailsState.Success ->
            GameDetailsScreen(state = currentState, modifier = modifier, navHostController = navHostController)
    }
}

@Composable
private fun GameDetailsScreen(
    state: GameDetailsState.Success,
    modifier: Modifier,
    navHostController: NavHostController?,
) {
    val imageHeight = screenSize.width.dp * IMAGE_HEIGHT_FACTOR
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxWidth().height(imageHeight),
                model = state.gameDetails.backgroundImage,
                contentScale = ContentScale.Crop,
                contentDescription = state.gameDetails.name.toContentDescription(),
            )
            Spacer(modifier = Modifier.size(medium))
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = medium, end = medium),
                horizontalArrangement = Arrangement.spacedBy(small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                state.gameDetails.releaseDate?.let { releaseDate ->
                    ReleaseDateLabel(releaseDate)
                }
                PlatformLogoList(
                    platforms = state.gameDetails.platforms,
                    imageSize = 18.dp,
                    horizontalSpacing = tiny,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.size(small))
            Text(
                text = state.gameDetails.name,
                style = getTypography().h2,
                modifier = Modifier.fillMaxWidth().padding(start = medium, end = medium),
            )
            Spacer(modifier = Modifier.size(medium))
            ExpandableText(
                text = state.gameDetails.descriptionRaw,
                textStyle = getTypography().body2,
                modifier = Modifier.fillMaxWidth().padding(start = medium, end = medium),
            )
            Spacer(modifier = Modifier.size(large))
        }
        IconButton(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(start = medium, top = medium)
                    .withCircleRippleEffect(),
            onClick = {
                navHostController?.popBackStack()
            },
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_arrow_back),
                tint = white,
                contentDescription = stringResource(Res.string.back_button_content_description),
            )
        }
    }
}

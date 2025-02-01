package dev.vladleesi.braindanceapp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.bundle.Bundle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import dev.vladleesi.braindanceapp.routes.GameDetailsRoute
import dev.vladleesi.braindanceapp.system.isLargeDevice
import dev.vladleesi.braindanceapp.system.screenSize
import dev.vladleesi.braindanceapp.system.toPx
import dev.vladleesi.braindanceapp.ui.components.ExpandableText
import dev.vladleesi.braindanceapp.ui.components.GenreTags
import dev.vladleesi.braindanceapp.ui.components.GlobalLoading
import dev.vladleesi.braindanceapp.ui.components.PlatformLogoList
import dev.vladleesi.braindanceapp.ui.components.ReleaseDateLabel
import dev.vladleesi.braindanceapp.ui.components.SpacerStatusBarInsets
import dev.vladleesi.braindanceapp.ui.components.StatusBarOverlay
import dev.vladleesi.braindanceapp.ui.components.TopAppBar
import dev.vladleesi.braindanceapp.ui.components.storesBlockItem
import dev.vladleesi.braindanceapp.ui.style.background
import dev.vladleesi.braindanceapp.ui.style.large
import dev.vladleesi.braindanceapp.ui.style.medium
import dev.vladleesi.braindanceapp.ui.style.overlayBlackWith70Alpha
import dev.vladleesi.braindanceapp.ui.style.small
import dev.vladleesi.braindanceapp.ui.style.tiny
import dev.vladleesi.braindanceapp.ui.style.topBarHeight
import dev.vladleesi.braindanceapp.ui.style.topBarHeightWithInsets
import dev.vladleesi.braindanceapp.ui.style.white
import dev.vladleesi.braindanceapp.ui.viewmodels.GameDetails
import dev.vladleesi.braindanceapp.ui.viewmodels.GameDetailsState
import dev.vladleesi.braindanceapp.ui.viewmodels.GameDetailsViewModel
import dev.vladleesi.braindanceapp.utils.toContentDescription

private const val IMAGE_HEIGHT_FACTOR = (9f / 16f)
private const val IMAGE_WIDTH_FACTOR_LARGE_SCREEN = 0.3f

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
    val lazyListState = rememberLazyListState()
    val (topBarColor, backButtonColor, topBarTitleColor) =
        calculateScrollTopBarColors(lazyListState = lazyListState)
    val isLargeDevice = isLargeDevice()

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(background),
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = modifier.fillMaxSize(),
        ) {
            item { SpacerStatusBarInsets() }
            if (isLargeDevice) {
                gameInfoHeaderLargeScreen(state)
            } else {
                gameInfoHeaderSmallScreen(state)
            }
            item {
                ExpandableText(
                    text = state.gameDetails.descriptionRaw,
                    modifier = Modifier.fillMaxWidth().padding(start = medium, end = medium),
                )
            }
            item { Spacer(modifier = Modifier.size(large)) }
            storesBlockItem(state.gameDetails.stores)
            item { Spacer(modifier = Modifier.size(large)) }
        }
        Column {
            StatusBarOverlay()
            TopAppBar(
                tabBarColor = topBarColor,
                titleColor = topBarTitleColor,
                showBackButton = true,
                backButtonBackgroundColor = backButtonColor,
                onBackButtonPressed = { navHostController?.popBackStack() },
                title = state.gameDetails.name,
            )
        }
    }
}

@Composable
private fun GameDetailsInfo(gameDetails: GameDetails) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = medium, end = medium),
        horizontalArrangement = Arrangement.spacedBy(small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        gameDetails.releaseDate?.let { releaseDate ->
            ReleaseDateLabel(releaseDate)
        }
        PlatformLogoList(
            platforms = gameDetails.platforms,
            imageSize = 18.dp,
            horizontalSpacing = tiny,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun calculateScrollTopBarColors(lazyListState: LazyListState): Triple<Color, Color, Color> {
    val targetColor = overlayBlackWith70Alpha
    val maxScrollOffsetPx = topBarHeightWithInsets.toPx()
    var firstOffset by remember { mutableStateOf(0f) }

    val combinedOffset =
        remember(
            lazyListState.firstVisibleItemIndex,
            lazyListState.firstVisibleItemScrollOffset,
        ) {
            val scrollOffset = lazyListState.firstVisibleItemScrollOffset.toFloat()
            when (lazyListState.firstVisibleItemIndex) {
                0 -> scrollOffset.also { firstOffset = scrollOffset }
                1 -> scrollOffset + firstOffset
                else -> maxScrollOffsetPx
            }
        }

    val fraction = (combinedOffset / maxScrollOffsetPx).coerceIn(0f, 1f)

    return Triple(
        lerp(Color.Transparent, targetColor, fraction),
        lerp(targetColor, Color.Transparent, fraction),
        lerp(Color.Transparent, white, fraction),
    )
}

private fun LazyListScope.gameInfoHeaderLargeScreen(state: GameDetailsState.Success) =
    item {
        val imageWidth = screenSize.width.dp * IMAGE_WIDTH_FACTOR_LARGE_SCREEN
        val imageHeight = imageWidth * IMAGE_HEIGHT_FACTOR
        Row(
            modifier =
                Modifier
                    .padding(horizontal = medium)
                    .padding(top = topBarHeight, bottom = medium),
        ) {
            AsyncImage(
                modifier =
                    Modifier
                        .width(imageWidth)
                        .height(imageHeight)
                        .clip(RoundedCornerShape(small)),
                model = state.gameDetails.backgroundImage,
                contentScale = ContentScale.Crop,
                contentDescription = state.gameDetails.name.toContentDescription(),
            )
            Spacer(modifier = Modifier.size(medium))
            Column {
                GameDetailsInfo(gameDetails = state.gameDetails)
                Spacer(modifier = Modifier.size(small))
                Text(
                    text = state.gameDetails.name,
                    style = MaterialTheme.typography.h2,
                    modifier = Modifier.fillMaxWidth().padding(start = medium, end = medium),
                )
                GenreTags(state.gameDetails.genres, onClick = {})
            }
        }
    }

private fun LazyListScope.gameInfoHeaderSmallScreen(state: GameDetailsState.Success) {
    item {
        val imageHeight = screenSize.width.dp * IMAGE_HEIGHT_FACTOR
        AsyncImage(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(imageHeight),
            model = state.gameDetails.backgroundImage,
            contentScale = ContentScale.Crop,
            contentDescription = state.gameDetails.name.toContentDescription(),
        )
    }
    item { Spacer(modifier = Modifier.size(medium)) }
    item { GameDetailsInfo(gameDetails = state.gameDetails) }
    item { Spacer(modifier = Modifier.size(small)) }
    item {
        Text(
            text = state.gameDetails.name,
            style = MaterialTheme.typography.h2,
            modifier = Modifier.fillMaxWidth().padding(start = medium, end = medium),
        )
    }
    item { GenreTags(state.gameDetails.genres, onClick = {}) }
    item { Spacer(modifier = Modifier.size(medium)) }
}

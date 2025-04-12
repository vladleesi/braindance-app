package dev.vladleesi.braindanceapp.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.vladleesi.braindanceapp.routes.GameDetailsRoute
import dev.vladleesi.braindanceapp.routes.navigate
import dev.vladleesi.braindanceapp.ui.components.StaticText
import dev.vladleesi.braindanceapp.ui.style.Dimens
import dev.vladleesi.braindanceapp.ui.viewmodels.HomeState
import dev.vladleesi.braindanceapp.utils.shimmerEffect

private const val MINI_GAME_CARD_SKELETON_COUNT = 4

@Composable
fun MiniGameCardList(
    title: String,
    state: HomeState,
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    when (state) {
        HomeState.Loading ->
            MiniGameCardListSkeleton(
                title = title,
                modifier = modifier,
            )

        is HomeState.Success<*> ->
            MiniGameCardList(
                title = title,
                games = state.entities.filterIsInstance<MiniGameCardModel>(),
                modifier = modifier,
                navHostController = navHostController,
            )

        is HomeState.Error -> {
            // TODO: Show error
        }
    }
}

@Composable
private fun MiniGameCardListSkeleton(
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            StaticText(
                text = title,
                modifier =
                    Modifier
                        .padding(horizontal = Dimens.medium)
                        .align(Alignment.CenterStart),
                style = MaterialTheme.typography.h2,
            )
            Button(
                onClick = {},
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.subtitle2,
                    modifier =
                        Modifier
                            .width(58.dp)
                            .clip(RoundedCornerShape(Dimens.small))
                            .shimmerEffect(),
                )
            }
        }
        LazyRow(
            modifier = Modifier.padding(top = Dimens.small),
            horizontalArrangement = Arrangement.spacedBy(Dimens.small),
            userScrollEnabled = false,
            contentPadding = PaddingValues(horizontal = Dimens.medium),
        ) {
            items(MINI_GAME_CARD_SKELETON_COUNT) {
                MiniGameCardSkeleton()
            }
        }
    }
}

@Composable
private fun MiniGameCardList(
    title: String,
    games: List<MiniGameCardModel>,
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    val onCardClicked: (Int) -> Unit = { id ->
        navHostController.navigate(
            route = GameDetailsRoute,
            arguments = mapOf(GameDetailsRoute.Params.GAME_ID to id.toString()),
        )
    }
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            StaticText(
                text = title,
                modifier =
                    Modifier
                        .padding(horizontal = Dimens.medium)
                        .align(Alignment.CenterStart),
                style = MaterialTheme.typography.h2,
            )
            Button(
                onClick = {},
                elevation = null,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Text(
                    text = "See all",
                    style = MaterialTheme.typography.subtitle2,
                )
            }
        }
        LazyRow(
            modifier = Modifier.padding(top = Dimens.small),
            horizontalArrangement = Arrangement.spacedBy(Dimens.small),
            contentPadding = PaddingValues(horizontal = Dimens.medium),
        ) {
            items(games, key = { it.id }, contentType = { it }) { card ->
                MiniGameCard(card = card, onCardClicked = onCardClicked)
            }
        }
    }
}

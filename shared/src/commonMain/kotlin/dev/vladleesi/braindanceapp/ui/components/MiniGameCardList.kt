package dev.vladleesi.braindanceapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import dev.vladleesi.braindanceapp.routes.GameDetailsRoute
import dev.vladleesi.braindanceapp.routes.navigate
import dev.vladleesi.braindanceapp.ui.style.medium
import dev.vladleesi.braindanceapp.ui.style.small
import dev.vladleesi.braindanceapp.ui.viewmodels.HomeState

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

        is HomeState.Success ->
            MiniGameCardList(
                title = title,
                games = state.games,
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
        StaticText(text = title, modifier = Modifier.padding(horizontal = medium), style = MaterialTheme.typography.h2)
        LazyRow(
            modifier = Modifier.padding(top = small),
            horizontalArrangement = Arrangement.spacedBy(small),
            userScrollEnabled = false,
            contentPadding = PaddingValues(horizontal = medium),
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
        StaticText(text = title, modifier = Modifier.padding(horizontal = medium), style = MaterialTheme.typography.h2)
        LazyRow(
            modifier = Modifier.padding(top = small),
            horizontalArrangement = Arrangement.spacedBy(small),
            contentPadding = PaddingValues(horizontal = medium),
        ) {
            items(games, key = { it.id }, contentType = { it }) { card ->
                MiniGameCard(card = card, onCardClicked = onCardClicked)
            }
        }
    }
}

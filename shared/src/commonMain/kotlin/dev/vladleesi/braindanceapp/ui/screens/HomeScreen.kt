package dev.vladleesi.braindanceapp.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vladleesi.braindanceapp.ui.components.MiniGameCard
import dev.vladleesi.braindanceapp.ui.components.MiniGameCardList
import dev.vladleesi.braindanceapp.ui.style.large
import dev.vladleesi.braindanceapp.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel { HomeViewModel() },
    modifier: Modifier = Modifier,
) {
    val popular by viewModel.popular.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    MiniGameCardList(
        title = "Popular",
        cardList =
            popular.map { game ->
                MiniGameCard(
                    title = game.title,
                    backgroundImage = game.backgroundImage,
                )
            },
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = large),
    )
}

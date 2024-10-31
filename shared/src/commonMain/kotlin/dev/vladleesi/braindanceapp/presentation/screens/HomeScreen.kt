package dev.vladleesi.braindanceapp.presentation.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vladleesi.braindanceapp.presentation.components.MiniGameCard
import dev.vladleesi.braindanceapp.presentation.components.MiniGameCardList
import dev.vladleesi.braindanceapp.presentation.style.large
import dev.vladleesi.braindanceapp.presentation.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel { HomeViewModel() },
    modifier: Modifier = Modifier,
) {
    val popular by viewModel.popular.collectAsState()

//    val popular = listOf(
//        MiniGameCard(title = "asdasd", backgroundImage = "https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10b6bcdbc.jpg"),
//        MiniGameCard(title = "asdasd", backgroundImage = "https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10b6bcdbc.jpg"),
//        MiniGameCard(title = "asdasd", backgroundImage = "https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10b6bcdbc.jpg"),
//    )

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

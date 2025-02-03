package dev.vladleesi.braindanceapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import dev.vladleesi.braindanceapp.ui.style.miniGameCardHeight
import dev.vladleesi.braindanceapp.ui.style.miniGameCardWidth
import dev.vladleesi.braindanceapp.ui.style.small
import dev.vladleesi.braindanceapp.utils.ParentPlatformType
import dev.vladleesi.braindanceapp.utils.shimmerEffect
import dev.vladleesi.braindanceapp.utils.toContentDescription

private const val CARD_SHIMMER_WIDTH_FACTOR = 0.75f

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MiniGameCard(
    card: MiniGameCardModel,
    modifier: Modifier = Modifier,
    onCardClicked: (id: Int) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Card(
            modifier =
                Modifier
                    .height(miniGameCardHeight)
                    .width(miniGameCardWidth),
            onClick = { onCardClicked(card.id) },
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = card.backgroundImageUrl,
                contentScale = ContentScale.Crop,
                contentDescription = card.title.toContentDescription(),
            )
        }
    }
}

@Composable
fun MiniGameCardSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .height(miniGameCardHeight)
                    .width(miniGameCardWidth)
                    .clip(RoundedCornerShape(small))
                    .shimmerEffect(),
        )
    }
}

data class MiniGameCardModel(
    val id: Int,
    val title: String,
    val backgroundImageUrl: String,
    val platforms: List<ParentPlatformType>,
)

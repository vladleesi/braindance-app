package dev.vladleesi.braindanceapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import dev.vladleesi.braindanceapp.routes.GameDetailsRoute
import dev.vladleesi.braindanceapp.routes.navigate
import dev.vladleesi.braindanceapp.system.screenSize
import dev.vladleesi.braindanceapp.ui.style.getTypography
import dev.vladleesi.braindanceapp.ui.style.micro
import dev.vladleesi.braindanceapp.ui.style.miniGameCardHeight
import dev.vladleesi.braindanceapp.ui.style.small
import dev.vladleesi.braindanceapp.ui.style.tiny
import dev.vladleesi.braindanceapp.utils.ParentPlatformType
import dev.vladleesi.braindanceapp.utils.shimmerEffect
import dev.vladleesi.braindanceapp.utils.toContentDescription

private const val CARD_WIDTH_FACTOR = 0.6f
private const val CARD_SHIMMER_WIDTH_FACTOR = 0.75f

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MiniGameCard(
    card: MiniGameCardModel,
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    val cardWidth = screenSize.width.dp * CARD_WIDTH_FACTOR
    Column(modifier = modifier.fillMaxSize()) {
        Card(
            modifier =
                Modifier
                    .height(miniGameCardHeight)
                    .width(cardWidth),
            onClick = {
                navHostController.navigate(
                    route = GameDetailsRoute,
                    arguments = mapOf(GameDetailsRoute.Params.GAME_ID to card.id.toString()),
                )
            },
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = card.backgroundImageUrl,
                contentScale = ContentScale.Crop,
                contentDescription = card.title.toContentDescription(),
            )
        }
        Spacer(Modifier.height(small))
        PlatformLogoList(
            platforms = card.platforms,
            imageSize = 14.dp,
            horizontalSpacing = tiny,
            modifier = Modifier.width(cardWidth).padding(start = tiny),
        )
        Spacer(Modifier.height(micro))
        Text(
            text = card.title,
            style = getTypography().subtitle1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(cardWidth).padding(start = tiny, end = tiny),
        )
    }
}

@Composable
fun MiniGameCardSkeleton(modifier: Modifier = Modifier) {
    val cardWidth = screenSize.width.dp * CARD_WIDTH_FACTOR
    Column(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .height(miniGameCardHeight)
                    .width(cardWidth)
                    .clip(RoundedCornerShape(small))
                    .shimmerEffect(),
        )
        Spacer(Modifier.height(small))
        Box(
            modifier =
                Modifier
                    .height(20.dp)
                    .width(cardWidth / 2)
                    .padding(start = tiny, end = tiny)
                    .shimmerEffect(),
        )
        Spacer(Modifier.height(micro))
        Box(
            modifier =
                Modifier
                    .height(14.dp)
                    .width(cardWidth * CARD_SHIMMER_WIDTH_FACTOR)
                    .padding(start = tiny, end = tiny)
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

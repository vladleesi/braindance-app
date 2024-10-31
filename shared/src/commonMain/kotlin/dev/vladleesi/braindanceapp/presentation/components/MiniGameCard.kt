package dev.vladleesi.braindanceapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.vladleesi.braindanceapp.presentation.style.getTypography
import dev.vladleesi.braindanceapp.presentation.style.secondary_variant
import dev.vladleesi.braindanceapp.presentation.style.small
import dev.vladleesi.braindanceapp.system.screenSize

@Composable
fun MiniGameCard(
    card: MiniGameCard,
    modifier: Modifier = Modifier,
) {
    val cardWidth = screenSize.width.dp * CARD_WIDTH_FACTOR
    Column(modifier = modifier.fillMaxSize()) {
        Card(
            modifier =
                Modifier
                    .height(144.dp)
                    .width(cardWidth),
            backgroundColor = secondary_variant,
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = card.backgroundImage,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }
        Spacer(Modifier.height(small))
        Text(text = card.title, style = getTypography().subtitle1)
    }
}

private const val CARD_WIDTH_FACTOR = 0.6f

data class MiniGameCard(
    val title: String,
    val backgroundImage: String,
)

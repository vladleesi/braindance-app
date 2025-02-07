package dev.vladleesi.braindanceapp.ui.components.giveaways

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.vladleesi.braindanceapp.ui.style.Dimens
import dev.vladleesi.braindanceapp.ui.style.overlayBlackWith50Alpha
import dev.vladleesi.braindanceapp.ui.style.white
import dev.vladleesi.braindanceapp.ui.viewmodels.HomeStateEntity
import dev.vladleesi.braindanceapp.utils.ParentPlatformType
import dev.vladleesi.braindanceapp.utils.toContentDescription
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GiveawayItem(
    item: GiveawayItemModel,
    modifier: Modifier = Modifier,
    onCardClicked: (id: Int) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Card(
            modifier =
                Modifier
                    .height(Dimens.giveAwayCardHeight)
                    .width(Dimens.giveAwayCardWidth),
            onClick = { onCardClicked(item.id) },
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = item.image,
                    contentScale = ContentScale.Crop,
                    contentDescription = item.title.toContentDescription(),
                )
                Row(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(Dimens.tiny)
                            .background(
                                color = overlayBlackWith50Alpha,
                                shape = RoundedCornerShape(Dimens.small),
                            )
                            .padding(Dimens.tiny),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.micro),
                ) {
                    item.platforms.forEach { platform ->
                        if (platform.iconRes == null) return@forEach
                        Image(
                            modifier = Modifier.size(12.dp),
                            painter = painterResource(platform.iconRes),
                            colorFilter = ColorFilter.tint(white),
                            contentDescription = platform.name.toContentDescription(),
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(Dimens.small))
        Text(
            text = item.title,
            style = MaterialTheme.typography.caption,
            color = white,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(Dimens.giveAwayCardWidth),
        )
        Spacer(modifier = Modifier.height(Dimens.micro))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier =
                    Modifier
                        .border(Dimens.one, white, RoundedCornerShape(Dimens.tiny))
                        .padding(horizontal = Dimens.tiny),
            ) {
                Text(
                    text = "FREE",
                    color = white,
                    style = MaterialTheme.typography.caption,
                )
            }
            Spacer(modifier = Modifier.width(Dimens.tiny))
            Text(
                text = item.worth,
                style = MaterialTheme.typography.subtitle2,
                textDecoration = TextDecoration.LineThrough,
            )
        }
    }
}

data class GiveawayItemModel(
    val id: Int,
    val image: String,
    val title: String,
    val worth: String,
    val endDate: String,
    val platforms: List<ParentPlatformType>,
) : HomeStateEntity

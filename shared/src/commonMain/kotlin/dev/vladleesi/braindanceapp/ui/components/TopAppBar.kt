package dev.vladleesi.braindanceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.vladleesi.braindanceapp.resources.Res
import dev.vladleesi.braindanceapp.resources.back_button_content_description
import dev.vladleesi.braindanceapp.resources.ic_arrow_back
import dev.vladleesi.braindanceapp.ui.style.medium
import dev.vladleesi.braindanceapp.ui.style.overlayBlackWith70Alpha
import dev.vladleesi.braindanceapp.ui.style.topBarHeight
import dev.vladleesi.braindanceapp.ui.style.white
import dev.vladleesi.braindanceapp.utils.withCircleRippleEffect
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    subtitle: String = "",
    titleColor: Color = white,
    tabBarColor: Color = overlayBlackWith70Alpha,
    backButtonIcon: DrawableResource = Res.drawable.ic_arrow_back,
    showBackButton: Boolean = false,
    backButtonBackgroundColor: Color = overlayBlackWith70Alpha,
    backButtonColor: Color = white,
    onBackButtonPressed: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(topBarHeight)
                .background(tabBarColor),
    ) {
        if (showBackButton) {
            IconButton(
                modifier =
                    Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = medium)
                        .withCircleRippleEffect(backButtonBackgroundColor),
                onClick = (onBackButtonPressed),
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    imageVector = vectorResource(backButtonIcon),
                    tint = backButtonColor,
                    contentDescription = stringResource(Res.string.back_button_content_description),
                )
            }
        }
        if (title.isNotEmpty()) {
            Column(
                modifier =
                    Modifier
                        .align(Alignment.CenterStart)
                        .padding(horizontal = 80.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.body2,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.caption,
                        color = titleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

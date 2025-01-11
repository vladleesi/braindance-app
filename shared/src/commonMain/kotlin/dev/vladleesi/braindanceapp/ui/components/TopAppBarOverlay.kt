package dev.vladleesi.braindanceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.vladleesi.braindanceapp.resources.Res
import dev.vladleesi.braindanceapp.resources.back_button_content_description
import dev.vladleesi.braindanceapp.resources.ic_arrow_back
import dev.vladleesi.braindanceapp.ui.style.background
import dev.vladleesi.braindanceapp.ui.style.medium
import dev.vladleesi.braindanceapp.ui.style.white
import dev.vladleesi.braindanceapp.utils.withCircleRippleEffect
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun TopAppBarOverlay(
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackButtonPressed: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                background,
                                Color.Transparent,
                            ),
                    ),
                ),
    ) {
        if (showBackButton) {
            IconButton(
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(start = medium, top = medium)
                        .withCircleRippleEffect(),
                onClick = (onBackButtonPressed),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_arrow_back),
                    tint = white,
                    contentDescription = stringResource(Res.string.back_button_content_description),
                )
            }
        }
    }
}

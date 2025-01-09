package dev.vladleesi.braindanceapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import dev.vladleesi.braindanceapp.ui.style.white
import dev.vladleesi.braindanceapp.utils.PlatformTypeShort
import dev.vladleesi.braindanceapp.utils.toContentDescription
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlatformLogoList(
    platforms: List<PlatformTypeShort>,
    modifier: Modifier = Modifier,
    imageSize: Dp,
    horizontalSpacing: Dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
    ) {
        platforms.forEach { platform ->
            if (platform.iconRes == null) return@forEach
            Image(
                modifier = Modifier.size(imageSize),
                painter = painterResource(platform.iconRes),
                contentDescription = platform.name.toContentDescription(),
                colorFilter = ColorFilter.tint(white),
            )
        }
    }
}

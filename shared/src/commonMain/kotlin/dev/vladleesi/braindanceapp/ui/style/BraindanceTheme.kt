package dev.vladleesi.braindanceapp.ui.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun BraindanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    if (darkTheme.not()) {
        // ignore by now
    }
    val shapes =
        Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(12.dp),
        )
    MaterialTheme(
        colors = DarkColors,
        typography = getTypography(),
        shapes = shapes,
        content = content,
    )
}

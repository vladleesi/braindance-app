package io.github.vladleesi.braindanceapp.android.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@SuppressWarnings("UnusedPrivateMember")
@Composable
fun BraindanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(8.dp),
        large = RoundedCornerShape(12.dp)
    )
    MaterialTheme(
        colors = DarkColors,
        typography = Typography,
        shapes = shapes,
        content = content
    )
}

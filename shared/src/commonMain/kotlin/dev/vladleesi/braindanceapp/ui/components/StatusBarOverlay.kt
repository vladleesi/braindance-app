package dev.vladleesi.braindanceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.vladleesi.braindanceapp.ui.style.overlayBlackWith70Alpha
import dev.vladleesi.braindanceapp.ui.style.statusBarInsets

@Composable
fun StatusBarOverlay(
    modifier: Modifier = Modifier,
    backgroundColor: Color = overlayBlackWith70Alpha,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(statusBarInsets)
            .background(backgroundColor),
    )
}

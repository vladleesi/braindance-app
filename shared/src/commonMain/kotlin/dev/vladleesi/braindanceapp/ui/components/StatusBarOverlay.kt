package dev.vladleesi.braindanceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.vladleesi.braindanceapp.ui.style.overlayBlack

@Composable
fun StatusBarOverlay() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .background(overlayBlack),
    )
}

package dev.vladleesi.braindanceapp.ui.style

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val micro = 2.dp
val tiny = 4.dp
val small = 8.dp
val medium = 16.dp
val large = 32.dp
val extraLarge = 64.dp

val miniGameCardHeight = 144.dp
val miniGameCardWidth = 256.dp

val topBarHeight = 82.dp

val statusBarInsets
    @Composable
    get() = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

val topBarHeightWithInsets: Dp
    @Composable
    get() = statusBarInsets + topBarHeight

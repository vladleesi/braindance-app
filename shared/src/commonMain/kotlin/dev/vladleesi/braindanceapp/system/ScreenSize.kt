package dev.vladleesi.braindanceapp.system

import androidx.compose.runtime.Composable

@get:Composable
expect val screenSize: ScreenSize

data class ScreenSize(
    val width: Float,
    val height: Float,
)

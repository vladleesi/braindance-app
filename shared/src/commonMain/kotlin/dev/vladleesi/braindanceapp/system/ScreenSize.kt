package dev.vladleesi.braindanceapp.system

import androidx.compose.runtime.Composable

@Composable
expect fun getScreenSize(): ScreenSize

data class ScreenSize(
    val width: Float,
    val height: Float,
)

package dev.vladleesi.braindanceapp.system

import androidx.compose.runtime.Composable
import kotlinx.browser.window

actual val screenSize: ScreenSize
    @Composable
    get() =
        ScreenSize(
            width = window.innerWidth.toFloat(),
            height = window.innerHeight.toFloat(),
        )

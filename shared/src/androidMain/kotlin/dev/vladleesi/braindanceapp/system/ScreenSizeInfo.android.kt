package dev.vladleesi.braindanceapp.system

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
actual fun getScreenSize(): ScreenSize {
    val configuration = LocalConfiguration.current
    return ScreenSize(
        width = configuration.screenWidthDp.toFloat(),
        height = configuration.screenHeightDp.toFloat()
    )
}

package dev.vladleesi.braindanceapp.system

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenSize(): ScreenSize {
    val containerSize = LocalWindowInfo.current.containerSize
    return ScreenSize(
        width = containerSize.width.toFloat(),
        height = containerSize.height.toFloat()
    )
}

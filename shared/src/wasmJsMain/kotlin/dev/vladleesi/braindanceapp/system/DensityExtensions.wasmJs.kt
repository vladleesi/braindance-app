package dev.vladleesi.braindanceapp.system

import androidx.compose.ui.unit.Dp
import kotlinx.browser.window

actual fun Dp.toPx(): Float = value * window.devicePixelRatio.toFloat()

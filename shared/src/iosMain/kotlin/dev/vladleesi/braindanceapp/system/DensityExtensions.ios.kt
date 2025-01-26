package dev.vladleesi.braindanceapp.system

import androidx.compose.ui.unit.Dp
import platform.UIKit.UIScreen

actual fun Dp.toPx(): Float {
    return this.value * UIScreen.mainScreen.scale.toFloat()
}

package dev.vladleesi.braindanceapp.system

import androidx.compose.ui.unit.Dp

actual fun Dp.toPx(): Float {
    val density = android.content.res.Resources.getSystem().displayMetrics.density
    return this.value * density
}

package io.github.vladleesi.braindanceapp.android.style

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val white = Color(0xFFEFEFEF)
val background = Color(0xFF171717)
val secondary_text = Color(0xFF969696)
val hint_text = Color(0xFF363638)
val secondary = Color(0xFF2F2F2F)
val secondary_variant = Color(0xFF2D3037)
val error = Color(0xFFEA5A4A)

// TODO: Needs to check all colors
val DarkColors = darkColors(
    primary = background,
    secondary = secondary,
    secondaryVariant = secondary_variant,
    background = background,
    surface = background,
    error = error
)

// TODO: Needs to implement?
val LightColors = lightColors()

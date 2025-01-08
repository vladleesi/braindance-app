package dev.vladleesi.braindanceapp.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import dev.vladleesi.braindanceapp.ui.style.overlayBlack

fun Modifier.withCircleRippleEffect() = then(Modifier.background(color = overlayBlack, shape = CircleShape))

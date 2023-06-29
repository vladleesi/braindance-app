package dev.vladleesi.braindanceapp.android.style

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.vladleesi.braindanceapp.android.R

private val fonts = FontFamily(
    Font(R.font.quicksand_light, FontWeight.Light),
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_medium, FontWeight.Medium),
    Font(R.font.quicksand_semibold, FontWeight.SemiBold),
    Font(R.font.quicksand_bold, FontWeight.Bold)
)

val Typography = Typography(
    h1 = TextStyle(
        color = white,
        fontFamily = fonts,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp
    ),
    h2 = TextStyle(
        color = white,
        fontFamily = fonts,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    body1 = TextStyle(
        color = white,
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    body2 = TextStyle(
        color = secondary_text,
        fontFamily = fonts,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    subtitle1 = TextStyle(
        color = white,
        fontFamily = fonts,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    subtitle2 = TextStyle(
        color = secondary_text,
        fontFamily = fonts,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)

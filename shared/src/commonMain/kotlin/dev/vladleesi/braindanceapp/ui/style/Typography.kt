package dev.vladleesi.braindanceapp.ui.style

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.vladleesi.braindanceapp.resources.Res
import dev.vladleesi.braindanceapp.resources.poppins_bold
import dev.vladleesi.braindanceapp.resources.poppins_light
import dev.vladleesi.braindanceapp.resources.poppins_medium
import dev.vladleesi.braindanceapp.resources.poppins_regular
import dev.vladleesi.braindanceapp.resources.poppins_semibold
import org.jetbrains.compose.resources.Font

@Composable
private fun getFonts() =
    FontFamily(
        Font(Res.font.poppins_light, FontWeight.Light),
        Font(Res.font.poppins_regular, FontWeight.Normal),
        Font(Res.font.poppins_medium, FontWeight.Medium),
        Font(Res.font.poppins_semibold, FontWeight.SemiBold),
        Font(Res.font.poppins_bold, FontWeight.Bold),
    )

@Composable
fun getTypography() =
    Typography(
        h1 =
            TextStyle(
                color = white,
                fontFamily = getFonts(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 40.sp,
            ),
        h2 =
            TextStyle(
                color = white,
                fontFamily = getFonts(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
            ),
        body1 =
            TextStyle(
                color = white,
                fontFamily = getFonts(),
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
            ),
        body2 =
            TextStyle(
                color = secondary_text,
                fontFamily = getFonts(),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
            ),
        subtitle1 =
            TextStyle(
                color = white,
                fontFamily = getFonts(),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            ),
        subtitle2 =
            TextStyle(
                color = secondary_text,
                fontFamily = getFonts(),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            ),
        caption =
            TextStyle(
                color = secondary_text,
                fontFamily = getFonts(),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            ),
    )

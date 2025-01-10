package dev.vladleesi.braindanceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import dev.vladleesi.braindanceapp.ui.style.background
import dev.vladleesi.braindanceapp.ui.style.getTypography
import dev.vladleesi.braindanceapp.ui.style.small
import dev.vladleesi.braindanceapp.ui.style.white

@Composable
fun ReleaseDateLabel(
    releaseDate: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = white,
    textColor: Color = background,
    textStyle: TextStyle = getTypography().subtitle1,
    shape: Shape = RoundedCornerShape(small),
    paddingValues: PaddingValues = PaddingValues(start = small, end = small),
) {
    Box(
        modifier =
            modifier
                .background(color = backgroundColor, shape = shape)
                .padding(paddingValues),
    ) {
        Text(
            text = releaseDate,
            style = textStyle,
            color = textColor,
        )
    }
}

package dev.vladleesi.braindanceapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.vladleesi.braindanceapp.presentation.style.getTypography
import dev.vladleesi.braindanceapp.presentation.style.medium
import dev.vladleesi.braindanceapp.presentation.style.small

@Composable
fun MiniGameCardList(
    title: String,
    cardList: List<MiniGameCard>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = title, modifier = Modifier.padding(horizontal = medium), style = getTypography().h2)
        LazyRow(
            modifier = Modifier.padding(top = small),
            horizontalArrangement = Arrangement.spacedBy(small),
            contentPadding = PaddingValues(horizontal = medium),
        ) {
            items(cardList) { card ->
                MiniGameCard(card)
            }
        }
    }
}

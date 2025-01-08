package dev.vladleesi.braindanceapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import dev.vladleesi.braindanceapp.ui.style.getTypography
import dev.vladleesi.braindanceapp.ui.style.medium
import dev.vladleesi.braindanceapp.ui.style.small

@Composable
fun MiniGameCardListComponent(
    title: String,
    cardList: List<MiniGameCard>,
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    Column(modifier = modifier) {
        Text(text = title, modifier = Modifier.padding(horizontal = medium), style = getTypography().h2)
        LazyRow(
            modifier = Modifier.padding(top = small),
            horizontalArrangement = Arrangement.spacedBy(small),
            contentPadding = PaddingValues(horizontal = medium),
        ) {
            items(cardList) { card ->
                MiniGameCardComponent(card = card, navHostController = navHostController)
            }
        }
    }
}

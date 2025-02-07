package dev.vladleesi.braindanceapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.vladleesi.braindanceapp.ui.components.SearchBar
import dev.vladleesi.braindanceapp.ui.components.SpacerStatusBarInsets
import dev.vladleesi.braindanceapp.ui.style.Dimens

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SpacerStatusBarInsets()
        SearchBar(modifier = Modifier.padding(horizontal = Dimens.medium).padding(top = Dimens.medium))
    }
}

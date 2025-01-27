package dev.vladleesi.braindanceapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.vladleesi.braindanceapp.ui.components.SpacerTopBarWithStatusBarInsets
import dev.vladleesi.braindanceapp.ui.style.medium

@Composable
fun NewsScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SpacerTopBarWithStatusBarInsets()
        Text(
            modifier = Modifier.padding(horizontal = medium),
            text = "News Screen",
            style = MaterialTheme.typography.h2,
        )
    }
}

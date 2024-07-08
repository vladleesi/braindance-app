package dev.vladleesi.braindanceapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BraindanceApp(modifier: Modifier = Modifier) {
    MaterialTheme {
        Scaffold(modifier.fillMaxSize()) {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // TODO: Move common UI from androidApp
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            modifier = modifier.align(Alignment.Center),
            text = Greeting().greet()
        )
    }
}

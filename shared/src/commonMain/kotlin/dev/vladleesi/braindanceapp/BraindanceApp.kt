package dev.vladleesi.braindanceapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.vladleesi.braindanceapp.presentation.style.BraindanceTheme
import dev.vladleesi.braindanceapp.presentation.style.getTypography

@Composable
fun BraindanceApp(modifier: Modifier = Modifier) {
    BraindanceTheme {
        Scaffold(modifier.fillMaxSize()) { innerPadding ->
            MainScreen(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState()),
            )
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // TODO: Move common UI from androidApp
    Box(modifier = modifier) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            style = getTypography().h1,
            text = Greeting().greet(),
        )
    }
}

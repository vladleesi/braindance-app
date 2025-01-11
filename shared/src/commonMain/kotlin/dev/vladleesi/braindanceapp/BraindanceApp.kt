package dev.vladleesi.braindanceapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dev.vladleesi.braindanceapp.ui.components.BottomBar
import dev.vladleesi.braindanceapp.ui.components.NavigationGraph
import dev.vladleesi.braindanceapp.ui.style.BraindanceTheme
import dev.vladleesi.braindanceapp.utils.ImageLoaderInitializer

@Composable
fun BraindanceApp(modifier: Modifier = Modifier) {
    // TODO: Refactor
    val isInitialized = remember { mutableStateOf(false) }
    if (!isInitialized.value) {
        ImageLoaderInitializer.initialize()
        isInitialized.value = true
    }

    val navController = rememberNavController()

    BraindanceTheme {
        Scaffold(
            bottomBar = {
                BottomBar(navController = navController)
            },
            modifier =
                modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
        ) { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
            ) {
                NavigationGraph(navController = navController)
            }
        }
    }
}

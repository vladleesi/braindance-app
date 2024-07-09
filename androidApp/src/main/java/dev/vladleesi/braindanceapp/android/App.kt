package dev.vladleesi.braindanceapp.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dev.vladleesi.braindanceapp.android.components.BottomBar
import dev.vladleesi.braindanceapp.android.components.BottomBarItem
import dev.vladleesi.braindanceapp.android.components.NavigationGraph
import dev.vladleesi.braindanceapp.android.components.TopBar
import dev.vladleesi.braindanceapp.presentation.style.BraindanceTheme

@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var label by remember { mutableIntStateOf(BottomBarItem.HOME.title) }
    BraindanceTheme {
        Scaffold(
            topBar = { TopBar(label) },
            bottomBar = {
                BottomBar(
                    navController = navController,
                    updateLabel = { label = it },
                )
            },
            modifier = modifier.fillMaxSize(),
        ) { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
            ) {
                NavigationGraph(navController = navController)
            }
        }
    }
}

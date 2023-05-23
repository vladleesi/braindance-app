package io.github.vladleesi.braindanceapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import io.github.vladleesi.braindanceapp.Greeting
import io.github.vladleesi.braindanceapp.android.components.BottomBar
import io.github.vladleesi.braindanceapp.android.components.BottomBarItem
import io.github.vladleesi.braindanceapp.android.components.NavigationGraph
import io.github.vladleesi.braindanceapp.android.components.TopBar
import io.github.vladleesi.braindanceapp.android.style.BraindanceTheme
import io.github.vladleesi.braindanceapp.android.style.Typography
import io.github.vladleesi.braindanceapp.storage.token.AndroidContextProvider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidContextProvider.getContext = { applicationContext }
        setContent {
            BraindanceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var label by remember { mutableStateOf(BottomBarItem.HOME.title) }
    Scaffold(
        topBar = { TopBar(label) },
        bottomBar = {
            BottomBar(
                navController = navController,
                updateLabel = { label = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            NavigationGraph(navController = navController)
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    BraindanceTheme {
        Text(text = Greeting().greet(), style = Typography.h1)
    }
}

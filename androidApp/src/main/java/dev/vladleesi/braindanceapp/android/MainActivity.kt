package dev.vladleesi.braindanceapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import dev.vladleesi.braindanceapp.BraindanceApp
import dev.vladleesi.braindanceapp.Greeting
import dev.vladleesi.braindanceapp.ui.style.BraindanceTheme
import dev.vladleesi.braindanceapp.ui.style.background
import dev.vladleesi.braindanceapp.ui.style.getTypography
import dev.vladleesi.braindanceapp.ui.style.navBarColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(background.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(navBarColor.toArgb())
        )
        setContent {
            BraindanceApp()
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    BraindanceTheme {
        Text(text = Greeting().greet(), style = getTypography().h1)
    }
}

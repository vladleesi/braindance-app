package dev.vladleesi.braindanceapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.vladleesi.braindanceapp.BraindanceApp
import dev.vladleesi.braindanceapp.Greeting
import dev.vladleesi.braindanceapp.ui.style.BraindanceTheme
import dev.vladleesi.braindanceapp.ui.style.getTypography

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

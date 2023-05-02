package io.github.vladleesi.braindanceapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vladleesi.braindanceapp.Greeting
import io.github.vladleesi.braindanceapp.android.style.MyApplicationTheme
import io.github.vladleesi.braindanceapp.android.style.background
import io.github.vladleesi.braindanceapp.android.style.hint_text
import io.github.vladleesi.braindanceapp.android.style.secondary
import io.github.vladleesi.braindanceapp.android.style.secondary_text
import io.github.vladleesi.braindanceapp.android.style.secondary_variant
import io.github.vladleesi.braindanceapp.android.style.white

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GreetingView(Greeting().greet())
                }
            }
        }
    }
}

@Composable
fun GreetingView(text: String) {
    DesignKitPreview()
}

@Composable
fun DesignKitPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Text(text = "MaterialTheme.typography.h1", style = MaterialTheme.typography.h1)
        Text(text = "MaterialTheme.typography.h2", style = MaterialTheme.typography.h2)
        Text(text = "MaterialTheme.typography.body1", style = MaterialTheme.typography.body1)
        Text(text = "MaterialTheme.typography.body2", style = MaterialTheme.typography.body2)
        Text(
            text = "MaterialTheme.typography.subtitle1",
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = "MaterialTheme.typography.subtitle2",
            style = MaterialTheme.typography.subtitle2
        )
        Spacer(modifier = Modifier.padding(top = 24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "#EFEFEF",
                color = background,
                modifier = Modifier
                    .background(color = white, shape = MaterialTheme.shapes.medium)
                    .padding(8.dp)
            )
            Text(
                text = "#171717",
                color = white,
                modifier = Modifier
                    .background(color = background, shape = MaterialTheme.shapes.medium)
                    .border(width = 1.dp, color = white, shape = MaterialTheme.shapes.medium)
                    .padding(8.dp)
            )
            Text(
                text = "#969696",
                color = background,
                modifier = Modifier
                    .background(color = secondary_text, shape = MaterialTheme.shapes.medium)
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.padding(top = 4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "#363638",
                color = white,
                modifier = Modifier
                    .background(color = hint_text, shape = MaterialTheme.shapes.medium)
                    .padding(8.dp)
            )
            Text(
                text = "#2F2F2F",
                color = white,
                modifier = Modifier
                    .background(color = secondary, shape = MaterialTheme.shapes.medium)
                    .padding(8.dp)
            )
            Text(
                text = "#2D3037",
                color = white,
                modifier = Modifier
                    .background(color = secondary_variant, shape = MaterialTheme.shapes.medium)
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.padding(top = 4.dp))
        Row {
            Text(
                text = "#EA5A4A",
                color = background,
                modifier = Modifier
                    .background(
                        color = io.github.vladleesi.braindanceapp.android.style.error,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(8.dp)
            )
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}

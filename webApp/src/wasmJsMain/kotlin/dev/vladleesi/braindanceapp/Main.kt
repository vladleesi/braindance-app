package dev.vladleesi.braindanceapp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.vladleesi.braindanceapp.koin.initKoin
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin(debugHttpLogging = true)
    ComposeViewport(document.body!!) {
        BraindanceApp()
    }
}

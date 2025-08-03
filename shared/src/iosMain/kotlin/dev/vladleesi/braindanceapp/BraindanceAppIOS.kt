package dev.vladleesi.braindanceapp

import androidx.compose.ui.window.ComposeUIViewController
import dev.vladleesi.braindanceapp.koin.initKoin
import platform.UIKit.UIViewController

fun mainViewController(): UIViewController =
    ComposeUIViewController(configure = { initKoin() }) {
        BraindanceApp()
    }

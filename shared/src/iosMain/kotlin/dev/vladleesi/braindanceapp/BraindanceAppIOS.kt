package dev.vladleesi.braindanceapp

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun mainViewController(): UIViewController {
    return ComposeUIViewController {
        BraindanceApp()
    }
}

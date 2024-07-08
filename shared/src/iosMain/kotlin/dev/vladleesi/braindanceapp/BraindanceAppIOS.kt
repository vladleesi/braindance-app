package dev.vladleesi.braindanceapp

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        BraindanceApp()
    }
}

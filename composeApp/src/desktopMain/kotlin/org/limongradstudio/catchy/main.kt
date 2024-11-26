package org.limongradstudio.catchy

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Catchy",
    ) {
        App()
    }
}

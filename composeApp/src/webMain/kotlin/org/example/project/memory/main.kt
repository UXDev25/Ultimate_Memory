package org.example.project.memory

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    Napier.base(DebugAntilog())
    ComposeViewport {
        App()
    }
}
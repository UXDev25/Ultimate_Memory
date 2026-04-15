package org.example.project.memory.audio

import kotlinx.browser.document
import org.w3c.dom.HTMLAudioElement

actual class AudioPlayer {
    actual fun playSound() {
        val audio = document.createElement("audio") as HTMLAudioElement
        audio.src = "file.mp3"
        audio.play()
    }
}

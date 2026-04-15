package org.example.project.memory.audio

import javazoom.jl.player.Player

actual class AudioPlayer {
    actual fun playSound() {
        val inputStream = AudioPlayer::class.java.getResourceAsStream("/objectionfunk.mp3")
        val player = Player(inputStream)
        Thread { player.play() }.start()
    }
}

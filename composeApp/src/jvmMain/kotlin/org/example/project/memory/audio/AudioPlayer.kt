package org.example.project.memory.audio

import javazoom.jl.player.Player

actual class AudioPlayer {
    actual fun playSound() {
        val inputStream = AudioPlayer::class.java.getResourceAsStream("/flipcard.mp3")
        val player = Player(inputStream)
        Thread { player.play() }.start()
    }
    actual fun playVictory() {
        val inputStream = AudioPlayer::class.java.getResourceAsStream("/victory.mp3")
        val player = Player(inputStream)
        Thread { player.play() }.start()
    }
}

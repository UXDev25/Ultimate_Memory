package org.example.project.memory.audio

import android.media.MediaPlayer
import org.example.project.memory.AppContextHolder
import org.example.project.memory.R

actual class AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    actual fun playSound() {
        val context = AppContextHolder.context
        mediaPlayer = MediaPlayer.create(context, R.raw.objectionfunk)
        mediaPlayer?.start()
    }
}

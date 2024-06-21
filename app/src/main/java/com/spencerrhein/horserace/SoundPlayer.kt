package com.ingilizceevi.soundplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

class SoundPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun startSound(soundUri: Uri): MediaPlayer? {
        try {
            stopSound()  // Ensure any existing playback is stopped
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, soundUri)
                prepare()
                start()
                setOnErrorListener { mp, what, extra ->
                    Log.e("SoundPlayer", "MediaPlayer error: what=$what, extra=$extra")
                    releaseMediaPlayer()
                    true  // Error was handled
                }
            }
        } catch (e: Exception) {
            Log.e("SoundPlayer", "Error playing sound", e)
            return null
        }
        return mediaPlayer
    }

    fun stopSound() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            releaseMediaPlayer()
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            reset()
            release()
        }
        mediaPlayer = null
    }
}

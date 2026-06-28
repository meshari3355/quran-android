package com.quranapp.android.services

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.quranapp.android.models.Reciter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// ===== Audio Playback State =====

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val currentSurah: Int = 0,
    val currentAyah: Int = 0,
    val currentReciterId: Int = 0,
    val duration: Long = 0L,
    val position: Long = 0L,
    val repeatMode: RepeatMode = RepeatMode.NONE
)

enum class RepeatMode {
    NONE, ONE, ALL;

    fun next(): RepeatMode {
        return when (this) {
            NONE -> ONE
            ONE -> ALL
            ALL -> NONE
        }
    }
}

// ===== Audio Playback Listener =====

interface AudioPlaybackListener {
    fun onPlaybackStateChanged(state: AudioPlaybackState)
    fun onPlaybackCompleted()
    fun onError(error: String)
    fun onAyahChanged(surah: Int, ayah: Int)
}

// ===== Audio Playback Service =====

class AudioPlaybackService : MediaSessionService() {
    private val binder = AudioPlaybackBinder()
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var mediaSession: MediaSession? = null
    private var exoPlayer: ExoPlayer? = null
    private var currentSurah = 0
    private var currentAyah = 0
    private var currentReciterId = 0
    private var totalAyahsInSurah = 0
    private var audioCacheManager: AudioCacheManager? = null

    private val listeners = mutableListOf<AudioPlaybackListener>()

    companion object {
        private const val MEDIA_SESSION_TAG = "AudioPlaybackService"
    }

    inner class AudioPlaybackBinder : Binder() {
        fun getService(): AudioPlaybackService = this@AudioPlaybackService
    }

    override fun onCreate() {
        super.onCreate()
        initializePlayer()
    }

    override fun onDestroy() {
        releasePlayer()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    // Required by MediaSessionService
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    // ===== Initialization =====

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(this)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build()

        mediaSession = MediaSession.Builder(this, exoPlayer!!)
            .setId(MEDIA_SESSION_TAG)
            .build()

        exoPlayer?.addListener(PlayerEventListener())
    }

    private fun releasePlayer() {
        mediaSession?.release()
        exoPlayer?.release()
        exoPlayer = null
        mediaSession = null
    }

    // ===== Playback Control Methods =====

    fun playAyah(
        surah: Int,
        ayah: Int,
        reciterId: Int,
        cacheManager: AudioCacheManager
    ) {
        scope.launch {
            try {
                audioCacheManager = cacheManager
                currentSurah = surah
                currentAyah = ayah
                currentReciterId = reciterId
                totalAyahsInSurah = getTotalAyahsInSurah(surah)

                notifyAyahChanged(surah, ayah)

                val audioUrl = cacheManager.getAudioUrl(surah, ayah, reciterId)
                    .getOrThrow()

                val mediaItem = MediaItem.Builder()
                    .setUri(audioUrl)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle("Surah $surah, Ayah $ayah")
                            .setArtist("Reciter: $reciterId")
                            .build()
                    )
                    .build()

                exoPlayer?.apply {
                    setMediaItem(mediaItem)
                    prepare()
                    play()
                }

                updatePlaybackState()
            } catch (e: Exception) {
                notifyError(e.message ?: "Failed to play audio")
            }
        }
    }

    fun playAyahRange(
        surah: Int,
        startAyah: Int,
        endAyah: Int,
        reciterId: Int,
        cacheManager: AudioCacheManager
    ) {
        scope.launch {
            try {
                audioCacheManager = cacheManager
                currentSurah = surah
                currentReciterId = reciterId
                totalAyahsInSurah = getTotalAyahsInSurah(surah)

                val mediaItems = mutableListOf<MediaItem>()
                for (ayah in startAyah..endAyah) {
                    val audioUrl = cacheManager.getAudioUrl(surah, ayah, reciterId)
                        .getOrNull()
                    if (audioUrl != null) {
                        mediaItems.add(
                            MediaItem.Builder()
                                .setUri(audioUrl)
                                .setMediaMetadata(
                                    MediaMetadata.Builder()
                                        .setTitle("Surah $surah, Ayah $ayah")
                                        .build()
                                )
                                .build()
                        )
                    }
                }

                exoPlayer?.apply {
                    setMediaItems(mediaItems)
                    prepare()
                    play()
                }

                currentAyah = startAyah
                notifyAyahChanged(surah, startAyah)
                updatePlaybackState()
            } catch (e: Exception) {
                notifyError(e.message ?: "Failed to play audio range")
            }
        }
    }

    fun play() {
        exoPlayer?.play()
        updatePlaybackState()
    }

    fun pause() {
        exoPlayer?.pause()
        updatePlaybackState()
    }

    fun stop() {
        exoPlayer?.stop()
        updatePlaybackState()
    }

    fun nextAyah() {
        if (currentAyah < totalAyahsInSurah) {
            playAyah(
                currentSurah,
                currentAyah + 1,
                currentReciterId,
                audioCacheManager ?: return
            )
        } else {
            notifyPlaybackCompleted()
        }
    }

    fun previousAyah() {
        if (currentAyah > 1) {
            playAyah(
                currentSurah,
                currentAyah - 1,
                currentReciterId,
                audioCacheManager ?: return
            )
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }

    fun setRepeatMode(mode: RepeatMode) {
        val playerMode = when (mode) {
            RepeatMode.NONE -> Player.REPEAT_MODE_OFF
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
        }
        exoPlayer?.repeatMode = playerMode
    }

    fun getRepeatMode(): RepeatMode {
        return when (exoPlayer?.repeatMode) {
            Player.REPEAT_MODE_ONE -> RepeatMode.ONE
            Player.REPEAT_MODE_ALL -> RepeatMode.ALL
            else -> RepeatMode.NONE
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
    }

    // ===== Listener Management =====

    fun addPlaybackListener(listener: AudioPlaybackListener) {
        listeners.add(listener)
    }

    fun removePlaybackListener(listener: AudioPlaybackListener) {
        listeners.remove(listener)
    }

    // ===== State Getters =====

    fun getCurrentState(): AudioPlaybackState {
        return AudioPlaybackState(
            isPlaying = exoPlayer?.isPlaying ?: false,
            currentSurah = currentSurah,
            currentAyah = currentAyah,
            currentReciterId = currentReciterId,
            duration = exoPlayer?.duration ?: 0L,
            position = exoPlayer?.currentPosition ?: 0L,
            repeatMode = getRepeatMode()
        )
    }

    fun isPlaying(): Boolean = exoPlayer?.isPlaying ?: false

    // ===== Private Helper Methods =====

    private fun updatePlaybackState() {
        listeners.forEach { it.onPlaybackStateChanged(getCurrentState()) }
    }

    private fun notifyAyahChanged(surah: Int, ayah: Int) {
        currentAyah = ayah
        listeners.forEach { it.onAyahChanged(surah, ayah) }
    }

    private fun notifyPlaybackCompleted() {
        listeners.forEach { it.onPlaybackCompleted() }
    }

    private fun notifyError(error: String) {
        listeners.forEach { it.onError(error) }
    }

    private fun getTotalAyahsInSurah(surah: Int): Int {
        val ayahsPerSurah = intArrayOf(
            7, 286, 200, 176, 120, 165, 206, 75, 129, 109, 123, 111, 43, 52, 99, 128, 111, 110,
            98, 135, 112, 78, 118, 64, 77, 227, 93, 88, 69, 60, 30, 73, 54, 45, 83, 182, 88, 75,
            85, 54, 53, 89, 59, 37, 35, 38, 29, 18, 45, 60, 49, 78, 48, 45, 90, 80, 61, 50, 45,
            33, 34, 39, 28, 34, 31, 34, 34, 28, 80, 30, 31, 29, 32, 31, 29, 34, 30, 30, 29, 30,
            30, 26, 28, 27, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30, 29, 29, 29, 30, 30, 30,
            30, 29, 29, 29, 29, 28, 29, 30, 29, 29, 29, 29, 30, 29, 29, 29, 28, 29, 29, 29, 29
        )
        return ayahsPerSurah.getOrNull(surah - 1) ?: 0
    }

    // ===== Player Event Listener =====

    private inner class PlayerEventListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            updatePlaybackState()
            if (playbackState == Player.STATE_ENDED) {
                if (getRepeatMode() == RepeatMode.ALL || getRepeatMode() == RepeatMode.NONE) {
                    nextAyah()
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlaybackState()
        }

        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
            notifyError(error.message ?: "Playback error")
        }
    }
}

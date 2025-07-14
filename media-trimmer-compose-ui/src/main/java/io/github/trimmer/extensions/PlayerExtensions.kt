package io.github.trimmer.extensions

import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged


val Player.durationFlow: Flow<Long>
    get() = callbackFlow {
        trySend(duration.coerceAtLeast(0L))
        val listener = object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                trySend(duration.coerceAtLeast(0L))
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                trySend(duration.coerceAtLeast(0L))
            }
        }
        addListener(listener)
        awaitClose { removeListener(listener) }
    }.distinctUntilChanged()

fun Player.currentPositionFlow(pollIntervalMs: Long = 300L): Flow<Long> = callbackFlow {
    val handler = Handler(Looper.getMainLooper())
    val pollingRunnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                trySend(currentPosition)
                handler.postDelayed(this, pollIntervalMs)
            } else {
                handler.removeCallbacks(this)
            }
        }
    }
    val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                handler.post(pollingRunnable)
            } else {
                handler.removeCallbacks(pollingRunnable)
            }
        }
    }
    addListener(listener)
    if (isPlaying) {
        handler.post(pollingRunnable)
    }
    awaitClose {
        handler.removeCallbacks(pollingRunnable)
        removeListener(listener)
    }
}.distinctUntilChanged()



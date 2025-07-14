package io.github.trimmer.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.media3.common.Player


@Composable
fun rememberMediaTrimmerStateWithPlayer(
    player: Player,
    initialStartMs: Long = 0L,
    initialEndMs: Long? = null,
    snapToFrame: Boolean = false,
    frameIntervalMs: Long = 1000L,
    minTrimDurationMs: Long = 1000L,
): MediaTrimmerState {

    val trimmerState = rememberMediaTrimmerState(
        durationMs = 0L,
        initialStartMs = initialStartMs,
        initialEndMs = initialEndMs,
        snapToFrame = snapToFrame,
        frameIntervalMs = frameIntervalMs,
        minTrimDurationMs = minTrimDurationMs
    )

    LaunchedEffect(player) {
        trimmerState.observe(player, initialEndMs, initialStartMs)
    }


    LaunchedEffect(trimmerState.progressMs, trimmerState.isUserSeeking) {
        if (trimmerState.isUserSeeking) {
            player.seekTo(trimmerState.progressMs)
            player.playWhenReady = false
        }
    }

    return trimmerState
}
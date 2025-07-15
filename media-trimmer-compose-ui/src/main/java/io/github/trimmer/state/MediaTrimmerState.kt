/*
 *  Copyright 2025 Amjd Alhashede
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package io.github.trimmer.state


import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import io.github.trimmer.extensions.currentPositionFlow
import io.github.trimmer.extensions.durationFlow
import io.github.trimmer.extensions.snapToFrame
import kotlinx.coroutines.flow.firstOrNull



@Composable
fun rememberMediaTrimmerState(
    durationMs: Long,
    initialStartMs: Long = 0L,
    initialEndMs: Long?=null,
    snapToFrame: Boolean = false,
    frameIntervalMs: Long = 1000L,
    minTrimDurationMs: Long = 1000L,
): MediaTrimmerState {
    return remember {
        MediaTrimmerState(
            durationMs, initialStartMs, initialEndMs,
            snapToFrame, frameIntervalMs, minTrimDurationMs
        )
    }
}



@Stable
class MediaTrimmerState internal constructor(
    durationStartMs: Long,
    initialStartMs: Long,
    initialEndMs: Long? = null,
    val snapToFrame: Boolean,
    val frameIntervalMs: Long,
    val minTrimDurationMs: Long,
    initialProgressMs: Long = initialStartMs,
) {
    var durationMs by mutableLongStateOf(durationStartMs)
        private set

    var startMs by mutableLongStateOf(initialStartMs)
        private set

    var endMs by mutableLongStateOf(initialEndMs?:durationStartMs)
        private set

    var progressMs by mutableLongStateOf(initialProgressMs)
        internal set

    var isProcessing by mutableStateOf(false)


    var isUserSeeking by mutableStateOf(false)
        internal set

    fun update(newStartMs: Long, newEndMs: Long) {
        val min = if (durationMs < minTrimDurationMs) durationMs else minTrimDurationMs
        val clampedEnd = newEndMs.coerceIn(newStartMs + min, durationMs)
        val clampedStart = newStartMs.coerceIn(0, clampedEnd - min)

        startMs = clampedStart.let { if (snapToFrame) it.snapToFrame(frameIntervalMs) else it }
        endMs = clampedEnd.let { if (snapToFrame) it.snapToFrame(frameIntervalMs) else it }

        updateProgress()
    }

    fun updateProgress(newProgressMs: Long = progressMs) {
        progressMs = newProgressMs.coerceIn(startMs, endMs)
    }

    fun updateDuration(newDuration: Long) {
        durationMs = newDuration.coerceAtLeast(1L)
    }

    suspend fun observe(player: Player, initialEndMs: Long?, initialStartMs: Long) {
        val realDuration = player.durationFlow.firstOrNull { it > 0L } ?: return

        updateDuration(realDuration)
        val finalEndMs = (initialEndMs?.coerceAtMost(realDuration) ?: realDuration)
        val finalStartMs = initialStartMs.coerceIn(0L, finalEndMs)
        update(finalStartMs, finalEndMs)

        player.seekTo(finalStartMs)
        player.playWhenReady = true

        player.currentPositionFlow().collect { newPosition ->
            if (!isUserSeeking) {
                if (newPosition < startMs || newPosition > endMs) {
                    player.pause()
                    player.seekTo(startMs)
                    updateProgress(startMs)
                }else{
                    updateProgress(newPosition)
                }
            }
        }
    }
}

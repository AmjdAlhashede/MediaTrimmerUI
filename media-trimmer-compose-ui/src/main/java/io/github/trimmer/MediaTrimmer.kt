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

package io.github.trimmer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import io.github.trimmer.components.TrimmerHandles
import io.github.trimmer.components.TrimmerOverlayAndPlayHead
import io.github.trimmer.components.TrimmerSurface
import io.github.trimmer.state.MediaTrimmerState
import io.github.trimmer.style.TrimmerColors
import io.github.trimmer.style.TrimmerStyle


/**
 * A highly customizable media trimmer UI component for Jetpack Compose.
 *
 * This composable provides a professional-looking and interactive UI for trimming audio or video.
 * It's purely for display and gesture handling; the actual media processing must be implemented
 * by the host application.
 *
 * @param state The [MediaTrimmerState] that holds and manages the trimmer's state.
 * @param modifier The modifier for this composable.
 * @param style The [TrimmerStyle] to customize the trimmer's appearance. Defaults to [TrimmerDefaults.style].
 */
@Composable
fun MediaTrimmer(
    state: MediaTrimmerState,
    modifier: Modifier = Modifier,
    colors: TrimmerColors = TrimmerDefaults.colors(),
    style: TrimmerStyle = TrimmerDefaults.style(),
    startHandle: @Composable (modifier: Modifier) -> Unit = {
        TrimmerDefaults.PillHandle(modifier = it)
    },
    endHandle: @Composable (modifier: Modifier) -> Unit = {
        TrimmerDefaults.PillHandle(modifier = it)
    },
    trackContent: @Composable BoxScope.(state: MediaTrimmerState) -> Unit = { state ->
        TrimmerDefaults.DefaultWaveform()
    },
) {
    var trackWidthPx by remember { mutableFloatStateOf(0f) }

    TrimmerSurface(
        modifier = modifier
            .fillMaxWidth()
            .height(style.trackHeight + style.containerContentPadding * 3),
        style = style,
        colors = colors,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(style.trackHeight)
                .padding(style.containerContentPadding)
                .onSizeChanged { size -> trackWidthPx = size.width.toFloat() }
        ) {
            val safeTrackWidthPx = if (trackWidthPx < 1f) 1f else trackWidthPx

            trackContent(state)


            val startPx by remember(state.startMs, safeTrackWidthPx) {
                derivedStateOf {
                    if (state.durationMs == 0L) 0f
                    else (state.startMs.toFloat() / state.durationMs) * safeTrackWidthPx
                }
            }

            val endPx by remember(state.endMs, safeTrackWidthPx) {
                derivedStateOf {
                    if (state.durationMs == 0L) 0f
                    (state.endMs.toFloat() / state.durationMs) * safeTrackWidthPx
                }
            }

            val playHeadPx by remember(state.progressMs, safeTrackWidthPx) {
                derivedStateOf {
                    if (state.durationMs == 0L) 0f
                    (state.progressMs.toFloat() / state.durationMs) * safeTrackWidthPx
                }
            }

            TrimmerOverlayAndPlayHead(
                startPx = startPx,
                endPx = endPx,
                playHeadPx = playHeadPx,
                colors = colors,
                style = style
            )

            TrimmerHandles(
                state = state,
                startPx = startPx,
                endPx = endPx,
                trackWidthPx = trackWidthPx,
                startHandle = startHandle,
                endHandle = endHandle,
                playHeadPx = playHeadPx,
                style = style,
                colors = colors
            )
        }
    }
}


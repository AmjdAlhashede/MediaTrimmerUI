package io.github.trimmer.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import io.github.trimmer.TrimmerDefaults
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
    trackContent: @Composable BoxScope.() -> Unit = {
        TrimmerDefaults.DefaultWaveform(
            modifier = Modifier.fillMaxSize(),
        )
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

            trackContent()


            val startPx = remember(state.startMs, safeTrackWidthPx) {
                if (state.durationMs == 0L) 0f
                else (state.startMs.toFloat() / state.durationMs) * safeTrackWidthPx
            }

            val endPx = remember(state.endMs, safeTrackWidthPx) {
                if (state.durationMs == 0L) 0f
                (state.endMs.toFloat() / state.durationMs) * safeTrackWidthPx
            }

            val playHeadPx = remember(state.progressMs, safeTrackWidthPx) {
                if (state.durationMs == 0L) 0f
                (state.progressMs.toFloat() / state.durationMs) * safeTrackWidthPx
            }

            TrimmerOverlayAndPlayHead(
                startPx = startPx,
                endPx = endPx,
                playHeadPx = playHeadPx,
                colors =  colors,
                playHeadWidth = style.playHeadWidth,
                selectionBorderWidth = style.selectionBorderWidth
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


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

package io.github.trimmer.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import io.github.trimmer.state.MediaTrimmerState
import io.github.trimmer.style.TrimmerColors
import io.github.trimmer.style.TrimmerStyle

/**
 * A dedicated composable for handling the trim handles and their drag gestures.
 * This ensures the heavy gesture logic is isolated from the main composable.
 *
 * @param state The [MediaTrimmerState] to update based on drag gestures.
 * @param startPx The current x-coordinate of the start handle in pixels.
 * @param endPx The current x-coordinate of the end handle in pixels.
 * @param trackWidthPx The total width of the track in pixels.
 * @param startHandle The composable for the start handle.
 * @param endHandle The composable for the end handle.
 * @param density The [LocalDensity] to use for pixel conversions.
 */
@Composable
internal fun TrimmerHandles(
    state: MediaTrimmerState,
    startPx: Float,
    endPx: Float,
    playHeadPx: Float,
    trackWidthPx: Float,
    style: TrimmerStyle,
    colors: TrimmerColors,
    startHandle: @Composable (modifier: Modifier) -> Unit,
    endHandle: @Composable (modifier: Modifier) -> Unit,
    density: Density = LocalDensity.current,
) {
    val minDistancePx = (state.minTrimDurationMs.toFloat() / state.durationMs) * trackWidthPx
    val selectionDuration = state.endMs - state.startMs

    val draggingColor = colors.draggingOverlayColor
    val draggingBorderColor = colors.draggingBorderColor
    val draggingBorderWidth = style.draggingBorderWidth
    val defaultBorderColor = colors.selectionBorder
    val defaultHandleColor = colors.handle
    val defaultBorderWidth = style.selectionBorderWidth

    var draggingHandle by remember { mutableStateOf<HandleType?>(null) }

    val animatedStartOffset by animateDpAsState(
        targetValue = with(density) { startPx.toDp() }, label = "StartOffset"
    )

    val animatedEndOffset by animateDpAsState(
        targetValue = with(density) { endPx.toDp() }, label = "EndOffset"
    )

    val animatedPlayHeadOffset by animateDpAsState(
        targetValue = with(density) { playHeadPx.toDp() }, label = "PlayHeadOffset"
    )

    val selectionDraggableState = rememberDraggableState { delta ->
        draggingHandle = HandleType.SELECTION
        val deltaMs = (delta / trackWidthPx) * state.durationMs
        val newStart =
            (state.startMs + deltaMs).coerceIn(0f, (state.durationMs - selectionDuration).toFloat())
        val newEnd = newStart + selectionDuration
        state.update(newStart.toLong(), newEnd.toLong())
    }

    val startDraggableState = rememberDraggableState { delta ->
        draggingHandle = HandleType.START
        val safeMinDistance = minDistancePx.coerceAtMost(endPx)
        val newStartPx = (startPx + delta).coerceIn(0f, endPx - safeMinDistance)
        val newStartMs = (newStartPx / trackWidthPx) * state.durationMs
        state.update(newStartMs.toLong(), state.endMs)
    }

    val endDraggableState = rememberDraggableState { delta ->
        draggingHandle = HandleType.END
        val newEndPx = (endPx + delta).coerceIn(startPx + minDistancePx, trackWidthPx)
        val newEndMs = (newEndPx / trackWidthPx) * state.durationMs
        state.update(state.startMs, newEndMs.toLong())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Draggable selection box
        Box(
            modifier = Modifier
                .offset(x = animatedStartOffset)
                .width(animatedEndOffset - animatedStartOffset)
                .fillMaxHeight()
                .border(
                    width = draggingHandle.borderWidthFor(
                        HandleType.SELECTION,
                        draggingBorderWidth,
                        defaultBorderWidth
                    ),
                    color = draggingHandle.borderColorFor(
                        HandleType.SELECTION,
                        draggingBorderColor,
                        defaultBorderColor
                    ),
                    shape = RoundedCornerShape(style.selectionCornerRadius)
                )
                .clip(RoundedCornerShape(style.selectionCornerRadius))
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = selectionDraggableState,
                    enabled = !state.isProcessing,
                    startDragImmediately = false,
                    onDragStarted = {
                        state.isUserSeeking = true
                        draggingHandle = HandleType.SELECTION
                    },
                    onDragStopped = {
                        state.isUserSeeking = false
                        draggingHandle = null
                    }
                )
        )

        // Start handle
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = animatedStartOffset - style.handleWidth / 2)
                .background(
                    color = draggingHandle.backgroundColorFor(
                        HandleType.START,
                        draggingColor,
                        defaultHandleColor
                    ),
                    shape = CircleShape
                )
                .border(
                    width = draggingHandle.borderWidthFor(
                        HandleType.START,
                        draggingBorderWidth,
                        defaultBorderWidth
                    ),
                    color = draggingHandle.borderColorFor(
                        HandleType.START,
                        draggingBorderColor,
                        defaultBorderColor
                    ),
                    shape = CircleShape
                )
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = startDraggableState,
                    enabled = !state.isProcessing,
                    startDragImmediately = false,
                    onDragStarted = {
                        state.isUserSeeking = true
                        draggingHandle = HandleType.START
                    },
                    onDragStopped = {
                        state.isUserSeeking = false
                        draggingHandle = null
                    }
                )
        ) {
            startHandle(Modifier)
        }

        // End handle
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = animatedEndOffset - style.handleWidth / 2)
                .background(
                    color = draggingHandle.backgroundColorFor(
                        HandleType.END,
                        draggingColor,
                        defaultHandleColor
                    ),
                    shape = CircleShape
                )
                .border(
                    width = draggingHandle.borderWidthFor(
                        HandleType.END,
                        draggingBorderWidth,
                        defaultBorderWidth
                    ),
                    color = draggingHandle.borderColorFor(
                        HandleType.END,
                        draggingBorderColor,
                        defaultBorderColor
                    ),
                    shape = CircleShape
                )
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = endDraggableState,
                    enabled = !state.isProcessing,
                    startDragImmediately = false,
                    onDragStarted = {
                        state.isUserSeeking = true
                        draggingHandle = HandleType.END
                    },
                    onDragStopped = {
                        state.isUserSeeking = false
                        draggingHandle = null
                    }
                )
        ) {
            endHandle(Modifier)
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = animatedPlayHeadOffset - style.handleWidth / 2)
                .width(style.playHeadWidth)
                .fillMaxHeight()
                .background(
                    color = colors.playHead,
                    shape = CircleShape
                )
                .border(
                    width = (style.playHeadWidth / 2),
                    color = Color.Transparent,
                    shape = CircleShape
                )
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val deltaMs = (delta / trackWidthPx) * state.durationMs
                        val newProgress = (state.progressMs + deltaMs).coerceIn(
                            state.startMs.toFloat(),
                            state.endMs.toFloat()
                        )
                        state.updateProgress(newProgress.toLong())
                    },
                    enabled = !state.isProcessing,
                    startDragImmediately = false,
                    onDragStarted = {
                        state.isUserSeeking = true
                        draggingHandle = null
                    },
                    onDragStopped = {
                        state.isUserSeeking = false
                    }
                )
        )
    }
}

private enum class HandleType {
    START, END, SELECTION
}

private fun HandleType?.backgroundColorFor(
    handleType: HandleType,
    draggingColor: Color,
    defaultColor: Color,
): Color {
    return if (this == handleType) draggingColor else defaultColor
}

private fun HandleType?.borderWidthFor(
    handleType: HandleType,
    draggingWidth: Dp,
    defaultWidth: Dp,
): Dp {
    return if (this == handleType) draggingWidth else defaultWidth
}

private fun HandleType?.borderColorFor(
    handleType: HandleType,
    draggingColor: Color,
    defaultColor: Color,
): Color {
    return if (this == handleType) draggingColor else defaultColor
}

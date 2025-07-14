package io.github.trimmer.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
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
// MediaTrimmer UI enhancement with animations and interactive feedback

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
        println("=====================================================Dragging START handle, delta=$delta")
        draggingHandle = HandleType.START
        val safeMinDistance = minDistancePx.coerceAtMost(endPx)
        val newStartPx = (startPx + delta).coerceIn(0f, endPx - safeMinDistance)
        val newStartMs = (newStartPx / trackWidthPx) * state.durationMs

        println("=====================================================Updating START to $newStartMs")
        state.update(newStartMs.toLong(), state.endMs)
    }


    val endDraggableState = rememberDraggableState { delta ->
        draggingHandle = HandleType.END
        val newEndPx = (endPx + delta).coerceIn(startPx + minDistancePx, trackWidthPx)
        val newEndMs = (newEndPx / trackWidthPx) * state.durationMs
        state.update(state.startMs, newEndMs.toLong())
    }


    val handleDragModifier = Modifier.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { state.isUserSeeking = true },
            onDragEnd = {
                state.isUserSeeking = false
                draggingHandle = null
            },
            onDragCancel = {
                state.isUserSeeking = false
                draggingHandle = null
            },
            onDrag = { _, _ -> }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Draggable selection box with always visible border
        Box(
            modifier = Modifier
                .offset(x = animatedStartOffset)
                .width(animatedEndOffset - animatedStartOffset)
                .fillMaxHeight()
                .background(
                    color = draggingColor.takeIf { draggingHandle == HandleType.SELECTION }
                        ?: colors.selectionOverlay
                )
                .border(
                    width = defaultBorderWidth,
                    color = defaultBorderColor
                )
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = selectionDraggableState,
                    enabled = !state.isProcessing,
                    startDragImmediately = false
                )
        )

        // Start handle with visible border and background
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = animatedStartOffset - style.handleWidth / 2)
                .background(
                    color = draggingColor.takeIf { draggingHandle == HandleType.START }
                        ?: defaultHandleColor,
                    shape = CircleShape
                )
                .border(
                    width = if (draggingHandle == HandleType.START) draggingBorderWidth else defaultBorderWidth,
                    color = if (draggingHandle == HandleType.START) draggingBorderColor else defaultBorderColor,
                    shape = CircleShape
                )
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = startDraggableState,
                    enabled = !state.isProcessing,
                    startDragImmediately = false
                )
        ) {
            startHandle(Modifier)
        }

        // End handle with visible border and background
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = animatedEndOffset - style.handleWidth / 2)
                .background(
                    color = draggingColor.takeIf { draggingHandle == HandleType.END }
                        ?: defaultHandleColor,
                    shape = CircleShape
                )
                .border(
                    width = if (draggingHandle == HandleType.END) draggingBorderWidth else defaultBorderWidth,
                    color = if (draggingHandle == HandleType.END) draggingBorderColor else defaultBorderColor,
                    shape = CircleShape
                )
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = endDraggableState,
                    enabled = !state.isProcessing,
                    startDragImmediately = false
                )
        ) {
            endHandle(Modifier)
        }

        // PlayHead with visible border and background
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = animatedPlayHeadOffset - style.handleWidth / 2)
                .width(style.playHeadWidth)
                .fillMaxHeight()
                .background(
                    color = draggingColor,
                    shape = CircleShape
                )
                .border(
                    width = draggingBorderWidth  ,
                    color = draggingBorderColor,
                    shape = CircleShape
                )
        )
    }
}

private enum class HandleType {
    START, END, SELECTION
}

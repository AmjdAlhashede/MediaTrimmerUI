package io.github.trimmer.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import io.github.trimmer.style.TrimmerColors

/**
 * A dedicated composable for drawing the selection overlay and playHead.
 * This composable will only recompose when its input parameters change.
 *
 * @param startPx The x-coordinate of the start of the selection in pixels.
 * @param endPx The x-coordinate of the end of the selection in pixels.
 * @param playHeadPx The x-coordinate of the playHead in pixels.
 * @param colors The [TrimmerColors] to use for drawing.
 * @param playHeadWidth The width of the playHead line in Dp.
 * @param selectionBorderWidth The width of the selection border line in pixels (default 3f).
 */@Composable
internal fun TrimmerOverlayAndPlayHead(
    startPx: Float,
    endPx: Float,
    playHeadPx: Float,
    colors: TrimmerColors,
    playHeadWidth: Dp,
    selectionBorderWidth: Dp
) {
    val density = LocalDensity.current
    val borderPx = with(density) { selectionBorderWidth.toPx() }
    val playHeadPxValue = with(density) { playHeadWidth.toPx() }

    Canvas(modifier = Modifier.fillMaxSize()) {

        drawRect(
            color = colors.containerBackgroundColor.copy(alpha = 0.5f),
            size = size
        )


        drawRect(
            color = colors.selectionOverlay,
            topLeft = Offset(x = startPx, y = 0f),
            size = Size(width = endPx - startPx, height = size.height)
        )

        drawLine(
            color = colors.selectionBorder,
            start = Offset(x = startPx, y = 0f),
            end = Offset(x = startPx, y = size.height),
            strokeWidth = borderPx
        )

        drawLine(
            color = colors.selectionBorder,
            start = Offset(x = endPx, y = 0f),
            end = Offset(x = endPx, y = size.height),
            strokeWidth = borderPx
        )

        drawLine(
            color = colors.playHead,
            start = Offset(x = playHeadPx, y = 0f),
            end = Offset(x = playHeadPx, y = size.height),
            strokeWidth = playHeadPxValue
        )
    }
}

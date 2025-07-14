package io.github.trimmer.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import io.github.trimmer.style.TrimmerColors
import io.github.trimmer.style.TrimmerStyle

/**
 * A dedicated composable for drawing the selection overlay and playHead.
 * This composable will only recompose when its input parameters change.
 *
 * @param startPx The x-coordinate of the start of the selection in pixels.
 * @param endPx The x-coordinate of the end of the selection in pixels.
 * @param playHeadPx The x-coordinate of the playHead in pixels.
 * @param colors The [TrimmerColors] to use for drawing.
 */
@Composable
internal fun TrimmerOverlayAndPlayHead(
    startPx: Float,
    endPx: Float,
    playHeadPx: Float,
    colors: TrimmerColors,
    style: TrimmerStyle,
) {

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cornerRadius = style.selectionCornerRadius.toPx()
        drawRoundRect(
            color = colors.containerBackgroundColor.copy(alpha = 0.5f),
            size = size,
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )
    }

}

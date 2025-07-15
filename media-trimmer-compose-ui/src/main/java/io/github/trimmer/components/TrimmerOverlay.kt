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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    Canvas(modifier = Modifier.fillMaxSize()
        .clip(RoundedCornerShape(style.selectionCornerRadius))
    ) {
        val cornerRadius = style.selectionCornerRadius.toPx()
        drawRoundRect(
            color = colors.containerBackgroundColor.copy(alpha = 0.5f),
            size = size,
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )
    }

}

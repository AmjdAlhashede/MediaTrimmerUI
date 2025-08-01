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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.trimmer.style.TrimmerColors
import io.github.trimmer.style.TrimmerStyle

@Composable
internal fun TrimmerSurface(
    modifier: Modifier = Modifier,
    style: TrimmerStyle,
    colors: TrimmerColors,
    content: @Composable BoxScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(style.containerCornerRadius),
        colors = CardDefaults.elevatedCardColors(containerColor = colors.containerBackgroundColor),
        border = BorderStroke(style.containerBorderWidth, colors.containerBorderColor),
    ) {
        Box(
            modifier = Modifier
                .padding(PaddingValues(style.containerContentPadding))
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            content = content
        )
    }
}

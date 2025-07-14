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
fun TrimmerSurface(
    modifier: Modifier = Modifier,
    style: TrimmerStyle,
    colors: TrimmerColors,
    content: @Composable BoxScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(style.containerCornerRadius),
        elevation = CardDefaults.elevatedCardElevation(style.containerShadowElevation),
        colors = CardDefaults.cardColors(containerColor = colors.containerBackgroundColor),
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

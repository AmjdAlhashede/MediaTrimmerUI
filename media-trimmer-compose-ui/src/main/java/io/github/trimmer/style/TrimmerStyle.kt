package io.github.trimmer.style

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Immutable
data class TrimmerColors(
    val containerBackgroundColor : Color,
    val containerBorderColor : Color,
    val handle: Color,
    val selectionOverlay: Color,
    val selectionBorder: Color,
    val playHead: Color,
    val draggingOverlayColor: Color,
    val draggingBorderColor: Color,
)

@Immutable
data class TrimmerStyle(
    val handleWidth: Dp,
    val handlerHeight: Dp,
    val trackHeight: Dp,
    val playHeadWidth: Dp,
    val selectionCornerRadius: Dp,
    val containerCornerRadius: Dp,
    val containerShadowElevation: Dp,
    val containerBorderWidth : Dp,
    val draggingBorderWidth: Dp,
    val containerContentPadding : Dp,
    val selectionBorderWidth: Dp
)
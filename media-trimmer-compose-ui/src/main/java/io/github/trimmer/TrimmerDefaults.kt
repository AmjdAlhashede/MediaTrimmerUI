package io.github.trimmer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.trimmer.style.TrimmerColors
import io.github.trimmer.style.TrimmerStyle
import kotlin.random.Random

/**
 * Internal object to hold default dimension tokens.
 * These are not exposed directly to the public API but are used internally for defaults.
 */

internal object TrimmerTokens {
    val handleWidth = 10.dp
    val playHeadWidth = 2.dp
    val trackHeight = 60.dp
    val handlerHeight = 60.dp
    val containerBorderWidth = 2.dp

    val shadowElevation = 4.dp
    val containerCornerRadius = 16.dp
    val handlerCornerRadius = 16.dp
    val draggingCornerRadius = 10.dp
    val draggingBorderWidth = 6.dp
    val containerContentPadding = 6.dp
    val pillHandleWidth = 10.dp
    val pillHandleHeight = 36.dp
    val pillHandleCornerRadius = 7.dp
    val selectionBorderWidth = 3.dp

    // Waveform defaults
    const val WAVE_FORM_BAR_SPACING = 2f
    const val WAVE_FORM_MAX_BAR_HEIGHT_FACTOR = 0.9f


}

/**
 * An object that provides default values and styles for the MediaTrimmer composable.
 */

@Immutable
object TrimmerDefaults {

    val scheme: ColorScheme
        @Composable get() = MaterialTheme.colorScheme

    @Composable
    fun colors(
        containerBackgroundColor: Color = scheme.surfaceVariant,
        containerBorderColor: Color = scheme.outline,
        handle: Color = scheme.primary,
        selectionOverlay: Color = scheme.surfaceContainerLowest.copy(alpha = 0.3f),
        selectionBorder: Color = scheme.primary.copy(alpha = 0.7f),
        playHead: Color = scheme.secondary,
        draggingOverlayColor: Color = scheme.tertiary.copy(alpha = 0.3f),
        draggingBorderColor: Color = scheme.tertiary,
    ): TrimmerColors {
        return TrimmerColors(
            containerBackgroundColor = containerBackgroundColor,
            containerBorderColor = containerBorderColor,
            handle = handle,
            selectionOverlay = selectionOverlay,
            selectionBorder = selectionBorder,
            playHead = playHead,
            draggingOverlayColor = draggingOverlayColor,
            draggingBorderColor = draggingBorderColor,
        )
    }

    @Composable
    fun PillHandle(
        modifier: Modifier = Modifier,
        color: Color = colors().handle,
        width: Dp = TrimmerTokens.pillHandleWidth,
        height: Dp = TrimmerTokens.pillHandleHeight,
        cornerRadius: Dp = TrimmerTokens.pillHandleCornerRadius,
        shadowElevation: Dp = TrimmerTokens.shadowElevation,
    ) {
        Box(
            modifier = modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(color)
                .shadow(shadowElevation, RoundedCornerShape(cornerRadius))
        )
    }


    @Composable
    fun DefaultWaveform(
        modifier: Modifier = Modifier,
        color: Color =colors().handle.copy(0.5f),
        waveformData: List<Float> = remember {
            List(100) { Random.nextFloat() * 0.9f + 0.1f }
        },
        barSpacing: Float = TrimmerTokens.WAVE_FORM_BAR_SPACING,
        maxBarHeightFactor: Float = TrimmerTokens.WAVE_FORM_MAX_BAR_HEIGHT_FACTOR,
        centerAlign: Boolean = true,
    ) {
        Canvas(modifier = modifier) {
            val barWidth = (size.width / waveformData.size) - barSpacing
            waveformData.forEachIndexed { index, amplitude ->
                val barHeight = size.height * amplitude * maxBarHeightFactor
                val xOffset = if (centerAlign) {
                    index * (barWidth + barSpacing)
                } else {
                    index * barWidth
                }
                drawRect(
                    color = color,
                    topLeft = Offset(x = xOffset, y = (size.height - barHeight) / 2),
                    size = size.copy(width = barWidth, height = barHeight)
                )
            }
        }
    }

    @Composable
    fun style(
        handleWidth: Dp = TrimmerTokens.handleWidth,
        handlerHeight: Dp = TrimmerTokens.handlerHeight,
        trackHeight: Dp = TrimmerTokens.trackHeight,
        playHeadWidth: Dp = TrimmerTokens.playHeadWidth,
        containerCornerRadius: Dp = TrimmerTokens.containerCornerRadius,
        containerShadowElevation: Dp = TrimmerTokens.shadowElevation,
        handlerCornerRadius: Dp = TrimmerTokens.handlerCornerRadius,
        draggingCornerRadius: Dp = TrimmerTokens.draggingCornerRadius,
        draggingBorderWidth: Dp = TrimmerTokens.draggingBorderWidth,
        containerContentPadding: Dp = TrimmerTokens.containerContentPadding,
        containerBorderWidth: Dp = TrimmerTokens.containerBorderWidth,
        selectionBorderWidth: Dp = TrimmerTokens.selectionBorderWidth
    ): TrimmerStyle {
        return TrimmerStyle(
            handleWidth = handleWidth,
            handlerHeight = handlerHeight,
            trackHeight = trackHeight,
            playHeadWidth = playHeadWidth,
            containerCornerRadius = containerCornerRadius,
            containerShadowElevation = containerShadowElevation,
            handlerCornerRadius = handlerCornerRadius,
            draggingCornerRadius = draggingCornerRadius,
            draggingBorderWidth = draggingBorderWidth,
            containerContentPadding = containerContentPadding,
            containerBorderWidth = containerBorderWidth,
            selectionBorderWidth = selectionBorderWidth
        )
    }
}

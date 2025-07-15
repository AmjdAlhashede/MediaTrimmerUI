package io.github

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import io.github.trimmer.TrimmerDefaults
import io.github.trimmer.state.rememberMediaTrimmerStateWithPlayer
import io.github.trimmer.MediaTrimmer


/**
 * A demo composable that shows the MediaTrimmer UI working in isolation.
 * The UI state can be observed and manipulated without a media player.
 */
@Composable
fun TrimmerShapeDemo() {
    val context = LocalContext.current

    val player = remember {
        ExoPlayer.Builder(context).build().also { exoplayer ->
            exoplayer.setMediaItem(
                MediaItem.fromUri("file:///android_asset/hi.mp4")
            )
            exoplayer.prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    val trimmerState = rememberMediaTrimmerStateWithPlayer(
        player = player,
        snapToFrame = true,
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        MediaTrimmer(
            state = trimmerState,
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = TrimmerDefaults.colors(
                containerBorderColor = Color.Transparent,
                containerBackgroundColor = Color.Transparent,
            ),
            style = TrimmerDefaults.style(
                trackHeight = 100.dp,
                containerShadowElevation = 0.dp
            ),
            trackContent = {state->
                DefaultVideoThumbnails(
                    state = state,
                    videoUri = "file:///android_asset/hi.mp4".toUri(),
                )
            }
        )

        Text(text = "Start: ${trimmerState.startMs} ms")
        Text(text = "End: ${trimmerState.endMs} ms")
        Text(text = "PlayHead: ${trimmerState.progressMs} ms")
        PlayPauseButton(player = player)
    }
}

@Preview
@Composable
fun TrimmerShapeDemoPreview() {
    TrimmerShapeDemo()
}

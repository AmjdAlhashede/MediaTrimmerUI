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

package io.github.mediatrimmer

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
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import io.github.trimmer.TrimmerDefaults
import io.github.trimmer.components.MediaTrimmer
import io.github.trimmer.state.rememberMediaTrimmerStateWithPlayer


/**
 * A demo composable that shows the MediaTrimmer UI working in isolation.
 * The UI state can be observed and manipulated without a media player.
 */
@Composable
fun MediaTrimmerDemo() {
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
        )

        Text(text = "Start: ${trimmerState.startMs} ms")
        Text(text = "End: ${trimmerState.endMs} ms")
        Text(text = "PlayHead: ${trimmerState.progressMs} ms")
        PlayPauseButton(player = player)
    }
}


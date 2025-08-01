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

import androidx.annotation.OptIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_SURFACE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import io.github.helper.VideoFrameExtractor
import io.github.trimmer.MediaTrimmer
import io.github.trimmer.TrimmerDefaults
import io.github.trimmer.state.MediaTrimmerState
import io.github.trimmer.state.rememberMediaTrimmerStateWithPlayer


/**
 * A demo composable that shows the MediaTrimmer UI working in isolation.
 * The UI state can be observed and manipulated without a media player.
 */
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoTrimmerDemo() {
    val videoUri = "file:///android_asset/istockphoto-1139869862-640_adpp_is.mp4".toUri()
    val context = LocalContext.current

    val player = remember {
        ExoPlayer.Builder(context).build().also { exoplayer ->
            exoplayer.setMediaItem(MediaItem.fromUri(videoUri))
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
    )


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.media_trimmer_title)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.media_trimmer_headline),
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = stringResource(R.string.media_trimmer_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    VideoPlayerWithPresentationState(
                        player = player,
                        modifier = Modifier.fillMaxSize()
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    MediaTrimmer(
                        state = trimmerState,
                        modifier = Modifier.weight(1f),
                        colors = TrimmerDefaults.colors(
                            containerBorderColor = Color.Transparent,
                            containerBackgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                        ),
                        trackContent = {
                            DefaultVideoThumbnails(
                                modifier = Modifier.fillMaxWidth(),
                                videoUri = videoUri,
                                state = trimmerState,
                            )
                        }
                    )

                    PlayPauseButton(
                        player = player,
                        modifier = Modifier.size(50.dp)
                    )
                }




                ElevatedButton(
                    onClick = { /* trim action */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.ContentCut, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.trim))
                }

                OutlinedButton(
                    onClick = { /* reset action */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.reset))
                }

                ElevatedButton(
                    onClick = { /* trim action */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.save))
                }
            }
        }
    )
}


@Composable
fun DefaultVideoThumbnails(
    modifier: Modifier = Modifier,
    videoUri: android.net.Uri,
    state: MediaTrimmerState,
    numberOfThumbnails: Int = 10,
    overlayColor: Color = Color.Black.copy(alpha = 0.5f),
) {
    val context = LocalContext.current
    // <--- Change 1: Instantiate VideoFrameExtractor as a remembered class instance
    val extractor =
        remember { VideoFrameExtractor(context.applicationContext) } // Pass applicationContext

    // <--- Change 2: Now storing Painters
    val thumbnails = remember { mutableStateListOf<Painter>() }
    var isLoadingThumbnails by remember { mutableStateOf(true) }

    LaunchedEffect(videoUri, state.durationMs, numberOfThumbnails) {
        if (state.durationMs > 0 && videoUri != android.net.Uri.EMPTY) {
            isLoadingThumbnails = true
            thumbnails.clear()
            // <--- Change 3: Call extractThumbnails on the instance
            val extracted = extractor.extractThumbnails(
                videoUri,
                state.durationMs,
                numberOfThumbnails
            )
            thumbnails.addAll(extracted)
            isLoadingThumbnails = false
        } else {
            thumbnails.clear()
            isLoadingThumbnails = false
        }
    }

    Box(modifier = modifier) {
        if (isLoadingThumbnails && thumbnails.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.3f))
            ) {
                // Consider adding a CircularProgressIndicator here
            }
        } else if (thumbnails.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = 0.1f))
            ) {
                // Consider adding a Text("No Video Frames") here
            }
        } else {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                thumbnails.forEach { painter -> // <--- Change 4: Iterate over Painters
                    Image(
                        painter = painter, // <--- Change 5: Use painter parameter directly
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val selectionStartRatio = state.startMs.toFloat() / state.durationMs
                val selectionEndRatio = state.endMs.toFloat() / state.durationMs
                val selectionStartX = width * selectionStartRatio
                val selectionEndX = width * selectionEndRatio

                drawRect(
                    color = overlayColor,
                    topLeft = Offset(0f, 0f),
                    size = Size(selectionStartX, height)
                )

                drawRect(
                    color = overlayColor,
                    topLeft = Offset(selectionEndX, 0f),
                    size = Size(width - selectionEndX, height)
                )
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerWithPresentationState(
    player: ExoPlayer,
    modifier: Modifier = Modifier,
) {
    val presentationState = rememberPresentationState(player)
    val scaledModifier =
        Modifier.resizeWithContentScale(ContentScale.Fit, presentationState.videoSizeDp)

    Box(modifier) {
        PlayerSurface(
            player = player,
            surfaceType = SURFACE_TYPE_SURFACE_VIEW,
            modifier = scaledModifier,
        )

        if (presentationState.coverSurface) {
            Box(Modifier.background(Color.Black))
        }
    }
}
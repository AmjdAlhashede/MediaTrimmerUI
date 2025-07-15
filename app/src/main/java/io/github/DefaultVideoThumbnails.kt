package io.github

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import io.github.helper.VideoFrameExtractor
import io.github.trimmer.state.MediaTrimmerState
import androidx.compose.ui.graphics.painter.Painter
import coil3.Uri

@Composable
fun DefaultVideoThumbnails(
    modifier: Modifier = Modifier,
    videoUri: android.net.Uri,
    state: MediaTrimmerState,
    numberOfThumbnails: Int = 10,
    overlayColor: Color = Color.Black.copy(alpha = 0.5f)
) {
    val context = LocalContext.current
    // <--- Change 1: Instantiate VideoFrameExtractor as a remembered class instance
    val extractor = remember { VideoFrameExtractor(context.applicationContext) } // Pass applicationContext

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
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
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
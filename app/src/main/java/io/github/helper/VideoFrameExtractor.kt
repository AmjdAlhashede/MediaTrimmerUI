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

package io.github.helper


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import coil3.ImageLoader
import coil3.compose.asPainter
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Scale
import coil3.video.VideoFrameDecoder
import coil3.video.videoFrameMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import coil3.size.Size
import androidx.compose.ui.graphics.painter.Painter
class VideoFrameExtractor(private val context: Context) {

    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(VideoFrameDecoder.Factory())
        }
        .build()


    /**
     * Extracts a list of video frame thumbnails from a given video URI using Coil.
     *
     * @param context The application context.
     * @param videoUri The URI of the video file.
     * @param durationMs The total duration of the video in milliseconds.
     * @param numberOfThumbnails The desired number of thumbnails to extract.
     * @return A list of [Bitmap] thumbnails.
     */
    suspend fun extractThumbnails(
        videoUri: Uri,
        durationMs: Long,
        numberOfThumbnails: Int,
    ): List<Painter> = withContext(Dispatchers.IO) {
        if (durationMs <= 0 || numberOfThumbnails <= 0) {
            return@withContext emptyList()
        }

        val intervalMs = durationMs / numberOfThumbnails.toFloat()

        val deferredBitmaps = (0 until numberOfThumbnails).map { i ->
            async {
                val timeMs = (i * intervalMs).toLong().coerceAtMost(durationMs - 1)
                val request = ImageRequest.Builder(context)
                    .data(videoUri)
                    .videoFrameMillis(timeMs)
                    .size(Size.ORIGINAL) // Or specify a fixed size: Size(width = 128, height = 128)
                    .scale(Scale.FIT)
                    .allowHardware(false)
                    .build()

                val result = imageLoader.execute(request)

                if (result is SuccessResult) {
                    (result.image.asPainter(context))
                } else {
                    null
                }
            }
        }
        deferredBitmaps.awaitAll().filterNotNull()
    }
}
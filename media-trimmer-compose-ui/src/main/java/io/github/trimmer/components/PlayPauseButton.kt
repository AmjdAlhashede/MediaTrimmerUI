package io.github.trimmer.components

import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState

@OptIn(UnstableApi::class)
@Composable
fun PlayPauseButton(player: Player, modifier: Modifier = Modifier) {
  val state = rememberPlayPauseButtonState(player)

  IconButton(onClick = state::onClick, modifier = modifier, enabled = state.isEnabled) {
    Icon(
      imageVector = if (state.showPlay) Icons.Default.PlayArrow else Icons.Rounded.Place,
      contentDescription = null
    )
  }
}
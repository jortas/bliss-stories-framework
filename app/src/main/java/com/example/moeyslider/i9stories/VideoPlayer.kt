package com.example.moeyslider.i9stories

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    link: Uri,
    thumbnail: Uri? = null,
    state: VideoPlayerState = VideoPlayerState.Unknown,
    onVideoChange: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val mediaItem = remember {
        MediaItem.fromUri(link)
    }

    val exoPlayer = remember { createExoPlayer(context, mediaItem, onVideoChange) }
    exoPlayer.playWhenReady = true


    // player view
    DisposableEffect(
        AndroidView(
            modifier = modifier
                .fillMaxSize()
                .clickable { exoPlayer.pause() },
            factory = {
                // exo player view for our video player
                PlayerView(context).apply {
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    player = exoPlayer
                    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                }
            }
        )
    ) {
        onDispose {
            // relase player when no longer needed
            exoPlayer.release()
        }
    }
}

private fun createExoPlayer(
    context: Context,
    mediaItem: MediaItem,
    onVideoChange: (Int) -> Unit
): ExoPlayer {
    return ExoPlayer.Builder(context).build().apply {
        this.setMediaItem(mediaItem)
        this.prepare()
        addListener(
            object : Player.Listener {
                override fun onEvents(
                    player: Player,
                    events: Player.Events
                ) {
                    super.onEvents(player, events)
                    if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)){

                    }
                }

                override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                    super.onPlaybackParametersChanged(playbackParameters)
                }

                override fun onMediaItemTransition(
                    mediaItem: MediaItem?,
                    reason: Int
                ) {
                    super.onMediaItemTransition(
                        mediaItem,
                        reason
                    )
                    // everytime media item changes notify playlist about current playing
                    onVideoChange(
                        this@apply.currentPeriodIndex
                    )
                    // everytime the media item changes show the title
                }
            }
        )
    }
}

enum class VideoPlayerState() {
    Playing, Paused, Unknown
}
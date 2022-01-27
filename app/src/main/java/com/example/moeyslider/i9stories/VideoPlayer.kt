package com.example.moeyslider.i9stories

import android.net.Uri
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.moeyslider.models.Story
import com.example.moeyslider.models.storyFactoryMock
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
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
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            this.setMediaItem(mediaItem)
            this.prepare()
            addListener(
                object : Player.Listener {
                    override fun onEvents(
                        player: Player,
                        events: Player.Events
                    ) {
                        super.onEvents(player, events)
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
    exoPlayer.playWhenReady = true

    // player view
    DisposableEffect(
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                // exo player view for our video player
                PlayerView(context).apply {
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

enum class VideoPlayerState() {
    Playing, Paused, Unknown
}
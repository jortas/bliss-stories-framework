package com.example.moeyslider.i9stories

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.delay

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoLinks: List<Uri>,
    thumbnail: Uri? = null,
    state: VideoPlayerState = VideoPlayerState.Playing,
    currentVideoIndex: Int,
    onStateChange: (VideoPlayerState) -> Unit = {},
    onVideoIndexChange: (Int) -> Unit = {},
    onVideoProgressChange: (Float) -> Unit = {}
) {
    val context = LocalContext.current
    val mediaItems = remember {
        videoLinks.map { MediaItem.fromUri(it) }
    }

    val exoPlayer =
        remember { createExoPlayer(context, mediaItems, onStateChange, onVideoIndexChange) }

    key(currentVideoIndex) {
        if (exoPlayer.currentMediaItemIndex != currentVideoIndex) {
            exoPlayer.seekTo(currentVideoIndex, 0L)
        }
    }

    remember(key1 = state) {
        exoPlayer.playWhenReady = state == VideoPlayerState.Playing
        1
    }
    LaunchedEffect(key1 = "init", block = {
        while (true) {
            onVideoProgressChange(exoPlayer.currentProgress())
            delay(PROGRESS_REFRESH_DELAY_MS)
        }
    })

    // player view
    DisposableEffect(
        AndroidView(
            modifier = modifier
                .fillMaxSize(),
            factory = {
                PlayerView(context).apply {
                    setKeepContentOnPlayerReset(true)
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

private fun ExoPlayer.currentProgress() =
    (currentPosition.toFloat() / duration.toFloat())

private fun createExoPlayer(
    context: Context,
    mediaItem: List<MediaItem>,
    onStateChange: (VideoPlayerState) -> Unit = {},
    onVideoChange: (Int) -> Unit,
): ExoPlayer {
    return ExoPlayer.Builder(context).build().apply {
        this.setMediaItems(mediaItem)
        this.prepare()
        addListener(
            object : Player.Listener {
                override fun onEvents(
                    player: Player,
                    events: Player.Events
                ) {
                    super.onEvents(player, events)
                    if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                        if (player.isPlaying) {
                            onStateChange(VideoPlayerState.Playing)
                        } else {
                            onStateChange(VideoPlayerState.Paused)
                        }
                    }
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
                        this@apply.currentMediaItemIndex
                    )
                }
            }
        )
    }
}

enum class VideoPlayerState() {
    Playing, Paused, Unknown
}

private const val PROGRESS_REFRESH_DELAY_MS = 200L
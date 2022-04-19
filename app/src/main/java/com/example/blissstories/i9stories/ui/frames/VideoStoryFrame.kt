package com.example.blissstories.i9stories.ui.frames

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.blissstories.i9stories.ui.StoryFrameState
import com.example.blissstories.i9stories.ui.currentProgress
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.delay

@Composable
fun VideoStoryFrame(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    currentVideoIndex: Int,
    onPlayingStateChange: (Boolean) -> Unit = {},
    onStoryProgressChange: (Float) -> Unit = {},
    onStoryFinished: () -> Unit = {}
) {
    val context = LocalContext.current
    var progress by remember(currentVideoIndex) {
        mutableStateOf(0f)
    }

    LaunchedEffect(currentVideoIndex) {
        if (exoPlayer.currentMediaItemIndex != currentVideoIndex) {
            exoPlayer.seekTo(currentVideoIndex, 0L)
        }
    }

    LaunchedEffect(key1 = "init", block = {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                super.onPlayWhenReadyChanged(playWhenReady, reason)
                if (reason == ExoPlayer.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM) {
                    onStoryFinished()
                } else {
                    onPlayingStateChange(playWhenReady)

                }
            }
        })
    })

    LaunchedEffect(currentVideoIndex) {
        if (exoPlayer.currentMediaItemIndex != currentVideoIndex) {
            exoPlayer.seekTo(currentVideoIndex, 0L)
        }
    }

    LaunchedEffect(progress) {
        onStoryProgressChange(exoPlayer.currentProgress())
    }

    LaunchedEffect("initial") {
        while(true) {
            delay(10)
            progress = exoPlayer.currentProgress()
        }
    }
    // player view
    DisposableEffect(
        AndroidView(
            modifier = modifier,
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
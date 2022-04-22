package com.example.blissstories.i9stories.ui.frames

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.blissstories.i9stories.ui.currentProgress
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.delay

@Composable
fun VideoStoryFrame(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    currentVideoIndex: Int,
    onFocus: Boolean,
    onPlayingStateChange: (Boolean) -> Unit = {},
    onStoryProgressChange: (Float) -> Unit = {},
    onStoryFinished: () -> Unit = {}
) {
    val context = LocalContext.current

    LaunchedEffect(currentVideoIndex) {
        exoPlayer.seekTo(currentVideoIndex, 0L)
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
package com.example.moeyslider.i9stories

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moeyslider.models.Story
import com.example.moeyslider.models.storyFactoryMock

@ExperimentalAnimationApi
@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    link: String,
    thumbnail: String? = null,
    currentPlaying: State<Int>,
    onVideoChange: (Int) -> Unit
) {

    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            this.setMediaItems(mediaItems)
            this.prepare()
            addListener(
                object : Player.Listener {
                    override fun onEvents(
                        player: Player,
                        events: Player.Events
                    ) {
                        super.onEvents(player, events)
                        // hide title only when player duration is at least 200ms
                        if (player.currentPosition >= 200)
                            visibleState.value = false
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
                        visibleState.value = true
                        videoTitle.value =
                            mediaItem?.mediaMetadata
                                ?.displayTitle.toString()
                    }
                }
            )
        }
    }

    // everytime an item in playlist is clicked play that video
    exoPlayer.seekTo(currentPlaying.value, C.TIME_UNSET)
    exoPlayer.playWhenReady = true

    // rest of things remain same
}
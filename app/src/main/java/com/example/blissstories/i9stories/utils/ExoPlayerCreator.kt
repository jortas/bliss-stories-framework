package com.example.blissstories.i9stories.utils

import android.content.Context
import com.example.blissstories.i9stories.StoryFrameState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

internal object ExoPlayerCreator {
    fun createExoPlayer(
        context: Context,
        mediaItem: List<MediaItem>,
        onStateChange: (StoryFrameState) -> Unit = {},
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
                                onStateChange(StoryFrameState.Playing)
                            } else {
                                onStateChange(StoryFrameState.Paused)
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
}
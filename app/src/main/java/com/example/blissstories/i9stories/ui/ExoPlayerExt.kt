package com.example.blissstories.i9stories.ui

import com.google.android.exoplayer2.ExoPlayer

internal fun ExoPlayer.currentProgress() =
    (currentPosition.toFloat() / duration.toFloat())

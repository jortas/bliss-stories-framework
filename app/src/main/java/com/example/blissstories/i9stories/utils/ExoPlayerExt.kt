package com.example.blissstories.i9stories

import androidx.compose.runtime.Composable
import com.google.android.exoplayer2.ExoPlayer

internal fun ExoPlayer.currentProgress() =
    (currentPosition.toFloat() / duration.toFloat())
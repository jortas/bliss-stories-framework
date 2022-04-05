package com.example.blissstories.i9stories.components

import androidx.annotation.FloatRange
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun StoryProgressBar(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    totalTime: Long = 0L,
    isPlaying: Boolean = false
) {

    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .background(
                Color.Gray, shape
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(
                    Color.White, shape
                )
        )
    }
}

@Preview
@Composable
private fun BlissSliderPreview() {
    MaterialTheme {
        Box(
            Modifier
                .fillMaxWidth()
                .height(16.dp)
        ) {
            StoryProgressBar(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f),
                progress = 1f
            )
        }
    }
}
package com.example.blissstories.i9stories

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
    @FloatRange(from = 0.0, to = 1.0) progress: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = getAnimationSpeed(progress),
            easing = LinearEasing
        )
    )

    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .padding(1.dp)
            .background(
                Color.Gray, shape
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
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

private fun getAnimationSpeed(progress: Float): Int {
    return if (progress == 0f) {
        0
    } else if (progress == 1f) {
        100
    } else {
        PROGRESS_ANIMATION_DURATION_MS
    }
}

private const val PROGRESS_ANIMATION_DURATION_MS = 200
package com.example.blissstories.i9stories

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ComposedStoryProgressBar(
    modifier: Modifier = Modifier,
    @IntRange(from = 0) numberOfStories: Int,
    @IntRange(from = 0) currentVideoIndex: Int,
    @FloatRange(from = 0.0, to = 1.0) progressOfCurrentVideo: Float
) {
    val storyIndicatorModifier = modifier
        .fillMaxWidth()
        .height(4.dp)

    if (currentVideoIndex >= numberOfStories) {
        throw RuntimeException("CurrentVideo Index $currentVideoIndex bigger than $numberOfStories")
    }

    Row(storyIndicatorModifier) {
        for (i in 0 until numberOfStories) {

            val progress = when(i) {
                in 0 until currentVideoIndex -> 1f
                currentVideoIndex -> progressOfCurrentVideo
                else -> 0f
            }
                StoryProgressBar(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(1f / (numberOfStories - i)),
                    progress = progress
                )
        }
    }
}

@Preview
@Composable
private fun ComposedStoryProgressPreview() {
    MaterialTheme {
        ComposedStoryProgressBar(
            numberOfStories = 4,
            currentVideoIndex = 3,
            progressOfCurrentVideo = 0.7f
        )
    }
}
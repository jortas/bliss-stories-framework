package com.example.blissstories.i9stories.components

import android.inputmethodservice.Keyboard
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.annotation.LongDef
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
    @IntRange(from = 0) currentStoryIndex: Int,
    @FloatRange(from = 0.0, to = 1.0) currentStoryProgress: Float
) {
    val storyIndicatorModifier = modifier
        .fillMaxWidth()
        .height(4.dp)

    if (currentStoryIndex >= numberOfStories) {
        throw RuntimeException("CurrentStory Index $currentStoryIndex bigger than $numberOfStories")
    }

    Row(storyIndicatorModifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 0 until numberOfStories) {
            val progress = when (i) {
                in 0 until currentStoryIndex -> 1f
                currentStoryIndex -> currentStoryProgress
                else -> 0f
            }

            if (currentStoryIndex == i) {
                StoryProgressBar(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(1f / (numberOfStories - i)),
                    progress = progress
                )
            } else {
                StoryProgressBar(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(1f / (numberOfStories - i)),
                    progress = progress
                )
            }
        }
    }
}

@Preview
@Composable
private fun ComposedStoryProgressPreview() {
    MaterialTheme {
        ComposedStoryProgressBar(
            numberOfStories = 4,
            currentStoryIndex = 3,
            currentStoryProgress = 0.7f
        )
    }
}
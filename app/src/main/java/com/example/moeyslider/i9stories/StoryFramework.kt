package com.example.moeyslider.i9stories

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moeyslider.models.Story
import com.example.moeyslider.models.storyFactoryMock

@Composable
fun StoryFramework(
    modifier: Modifier = Modifier,
    stories: List<Story>
) {
    val currentStoryIndex by remember { mutableStateOf(0) }
    val currentVideoProgress by remember { mutableStateOf(0) }

    ComposedStoryProgressBar(
        numberOfStories = stories.size,
        currentVideoIndex = currentStoryIndex,
        progressOfCurrentVideo = 0.7f
    )

    VideoPlayer(
        modifier = Modifier.fillMaxSize(),
        link = stories.first().video.link
    )
}

@Preview
@Composable
private fun StoryFrameworkPreview() {
    MaterialTheme {
        StoryFramework(
            modifier = Modifier.fillMaxSize(),
            stories = storyFactoryMock()
        )
    }
}
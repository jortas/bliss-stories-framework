package com.example.blissstories.i9stories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.blissstories.models.Story
import kotlinx.coroutines.delay

@Composable
fun StaticStoryPlayer(
    modifier: Modifier = Modifier,
    story: Story.Static,
    playerState: StoryFrameState,
    onStoryProgressChange: (Float) -> Unit = {},
    onStoryFinished: () -> Unit = {}
) {
    val context = LocalContext.current
    var progress by remember(story) {
        mutableStateOf(0f)
    }

    LaunchedEffect(story, playerState, progress) {
        if (progress < story.duration.timeInMs && playerState == StoryFrameState.Playing) {
            delay(UPDATE_PROGRESS_DELAY_MS)
            progress += UPDATE_PROGRESS_DELAY_MS
        }

        if (progress >= story.duration.timeInMs) {
            onStoryFinished()
            progress = 0f
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .background(color = story.color)
    )


    LaunchedEffect(onStoryProgressChange, progress, block = {
        onStoryProgressChange(progress / story.duration.timeInMs)
    })
}

internal const val UPDATE_PROGRESS_DELAY_MS = 200L
package com.example.blissstories.i9stories.ui.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.example.blissstories.models.Story
import com.example.blissstories.models.StoryPreview
import com.example.blissstories.models.StorySet
import com.google.android.exoplayer2.MediaItem

data class StorySetUiState(
    val storySet: StorySet
) {
    val preview: StoryPreview = storySet.preview
    val stories: List<Story> = storySet.stories

    var playing: Boolean = false
    var currentStoryIndex: Int = 0

    val videoMediaItems = stories.filterIsInstance(Story.Video::class.java)
        .map { MediaItem.fromUri(it.video) }

    val mediaItemsIndex = extractMediaItemsIndex(storySet, videoMediaItems)

    var currentProgress: Float = 0f

    val currentStory: Story
        get() = stories[currentStoryIndex]

    val currentStaticStory: Story.Static
        get() = stories[currentStoryIndex] as Story.Static

    val currentVideoIndex
        get() = mediaItemsIndex[currentStoryIndex]

    fun isInFirstStory(): Boolean = currentStoryIndex == 0
    fun isInLastStory(): Boolean = currentStoryIndex == stories.lastIndex

    fun resetProgress(){
        currentProgress = 0f
    }
}

//This val corresponds to the index of the video the exoplayer should be in
private fun extractMediaItemsIndex(
    storySet: StorySet,
    videoMediaItems: List<MediaItem>
): List<Int> {
    var nextIndex = 0
    var currentIndex: Int
    return storySet.stories.map {
        currentIndex = nextIndex
        if (it is Story.Video && currentIndex < videoMediaItems.size - 1) {
            nextIndex++
            currentIndex
        } else {
            currentIndex
        }
    }
}

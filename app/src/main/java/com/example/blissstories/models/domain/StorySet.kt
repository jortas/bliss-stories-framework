package com.example.blissstories.models.domain

import com.example.blissstories.models.api.StoryDto
import com.google.android.exoplayer2.MediaItem

data class StorySet(
    val preview: StoryPreview,
    val stories: List<Story>
) {

    var currentStoryIndex: Int = 0

    val videoMediaItems = stories.filterIsInstance(Story.Video::class.java)
        .map { MediaItem.fromUri(it.video) }

    private val mediaItemsIndex = extractMediaItemsIndex(stories, videoMediaItems)

    val currentStory: Story
        get() = stories[currentStoryIndex]

    val currentStaticStory: Story.Static
        get() = stories[currentStoryIndex] as Story.Static

    val currentVideoIndex
        get() = mediaItemsIndex[currentStoryIndex]

    fun isInFirstStory(): Boolean = currentStoryIndex == 0
    fun isInLastStory(): Boolean = currentStoryIndex == stories.lastIndex
}

//This val corresponds to the index of the video the exoplayer should be in
private fun extractMediaItemsIndex(
    storySet: List<Story>,
    videoMediaItems: List<MediaItem>
): List<Int> {
    var nextIndex = 0
    var currentIndex: Int
    return storySet.map {
        currentIndex = nextIndex
        if (it is Story.Video && currentIndex < videoMediaItems.size - 1) {
            nextIndex++
            currentIndex
        } else {
            currentIndex
        }
    }
}

package com.example.blissstories.models

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.blissstories.AquaGreen

typealias StorySet = List<Story>

sealed class Story() {
    abstract val order: Int

    data class Video(val video: Uri, override val order: Int) : Story()
    data class Static(
        val color: Color,
        val duration: Duration,
        val title: String,
        val description: String? = null,
        override val order: Int
    ) : Story()


    enum class Duration(val timeInMs: kotlin.Long) {
        Short(6000), Long(12000)
    }
}


fun storyFactoryMock(): MutableList<Story> {
    val storyList = mutableListOf<Story>()
    val story0 = Story.Video(
        Uri.parse(VIDEO1),
        order = 0,
    )
    val story1 = Story.Static(
        color = Color.Blue,
        duration = Story.Duration.Short,
        order = 1,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    val story2 = Story.Static(
        color = Color.Yellow,
        duration = Story.Duration.Short,
        order = 2,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    val story3 = Story.Video(
        Uri.parse(VIDEO1),
        order = 3
    )
    val story4 = Story.Static(
        color = Color.Gray,
        duration = Story.Duration.Short,
        order = 2,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    storyList.add(story0)
    storyList.add(story1)
    storyList.add(story2)
    storyList.add(story3)
    storyList.add(story4)
    return storyList
}


fun staticStoryFactoryMock(): MutableList<Story> {
    val storyList = mutableListOf<Story>()
    val story1 = Story.Static(
        color = Color.AquaGreen(),
        duration = Story.Duration.Short,
        order = 1,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    val story2 = Story.Static(
        color = Color.AquaGreen(),
        duration = Story.Duration.Short,
        order = 2,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    val story4 = Story.Static(
        color = Color.Gray,
        duration = Story.Duration.Short,
        order = 2,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    storyList.add(story1)
    storyList.add(story2)
    storyList.add(story4)
    return storyList
}

const val VIDEO1 =
    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
package com.example.blissstories.models.mocks

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.blissstories.projectutils.AquaGreen
import com.example.blissstories.R
import com.example.blissstories.models.Story
import com.example.blissstories.models.StoryPreview
import com.example.blissstories.models.StorySet
import com.example.blissstories.projectutils.resources.ColorKeyResource
import com.example.blissstories.projectutils.resources.ImageKeyResource

fun staticStoryFactoryMock(): StorySet {
    val preview = StoryPreview(
        "Title",
        ImageKeyResource(key = "image.jpg"),
        background = ColorKeyResource(key = "AquaGreen"),
        titleColor = ColorKeyResource(key = "White")
    )

    val storyList = mutableListOf<Story>()
    val story1 = Story.Static(
        color = Color.AquaGreen(),
        duration = Story.Static.Duration.Short,
        order = 1,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    val story2 = Story.Static(
        color = Color.AquaGreen(),
        duration = Story.Static.Duration.Long,
        order = 2,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    val story4 = Story.Static(
        color = Color.Gray,
        duration = Story.Static.Duration.Short,
        order = 2,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    storyList.add(story1)
    storyList.add(story2)
    storyList.add(story4)
    return StorySet(preview, storyList)
}

const val VIDEO1 =
    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"


fun storyFactoryMock(): MutableList<Story> {
    val storyList = mutableListOf<Story>()
    val story0 = Story.Video(
        Uri.parse(VIDEO1),
        order = 0,
    )
    val story1 = Story.Static(
        color = Color.Blue,
        duration = Story.Static.Duration.Short,
        order = 1,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    val story2 = Story.Static(
        color = Color.Yellow,
        duration = Story.Static.Duration.Short,
        order = 2,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    val story3 = Story.Video(
        Uri.parse(VIDEO1),
        order = 3
    )
    val story4 = Story.Static(
        color = Color.Gray,
        duration = Story.Static.Duration.Short,
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

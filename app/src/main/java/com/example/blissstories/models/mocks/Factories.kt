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

fun storyPreviewMock(): StoryPreview {
    return StoryPreview(
        "This Title has 36 characters. Title.",
        ImageKeyResource(key = "image.jpg"),
        background = ColorKeyResource(key = "AquaGreen"),
        titleColor = ColorKeyResource(key = "White")
    )
}


fun staticStoryFactoryMock(): StorySet {
    val preview = storyPreviewMock()

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


const val VIDEO2 =
    "https://player.vimeo.com/external/699535161.m3u8?s=7b4def605d92599f2353a08fc5b021bb3c2c642d"

//preview
const val VIDEO3 =
    "https://player.vimeo.com/external/699537879.m3u8?s=181c0f6abc3cdc16788469c9332030374150d502"

fun storyFactoryMock(): StorySet {
    val preview = storyPreviewMock()

    val storyList = mutableListOf<Story>()
    val story0 = Story.Video(
        Uri.parse(VIDEO2),
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

    return StorySet(preview, storyList)
}

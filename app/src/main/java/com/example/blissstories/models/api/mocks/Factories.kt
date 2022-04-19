package com.example.blissstories.models.api.mocks

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.blissstories.projectutils.AquaGreen
import com.example.blissstories.models.api.StoryDto
import com.example.blissstories.models.api.StoryPreviewDto
import com.example.blissstories.models.api.StorySetDto
import com.example.blissstories.models.domain.StorySet
import com.example.blissstories.models.mappers.toDomain
import com.example.blissstories.projectutils.resources.ColorKeyResource
import com.example.blissstories.projectutils.resources.ImageKeyResource

fun storyPreviewMock(): StoryPreviewDto {
    return StoryPreviewDto(
        "This Title has 36 characters. Title.",
        ImageKeyResource(key = "image.jpg"),
        background = ColorKeyResource(key = "AquaGreen"),
        titleColor = ColorKeyResource(key = "White")
    )
}

fun staticStoryFactoryMock(): StorySet {
    val preview = storyPreviewMock()

    val storyList = mutableListOf<StoryDto>()
    val story1 = StoryDto.Static(
        color = Color.AquaGreen(),
        duration = StoryDto.Static.Duration.Short,
        order = 1,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    val story2 = StoryDto.Static(
        color = Color.AquaGreen(),
        duration = StoryDto.Static.Duration.Long,
        order = 2,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    val story4 = StoryDto.Static(
        color = Color.Gray,
        duration = StoryDto.Static.Duration.Short,
        order = 2,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    storyList.add(story1)
    storyList.add(story2)
    storyList.add(story4)
    return StorySetDto(preview, storyList).toDomain()
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

    val storyList = mutableListOf<StoryDto>()
    val story0 = StoryDto.Video(
        Uri.parse(VIDEO2),
        order = 0,
    )
    val story1 = StoryDto.Static(
        color = Color.Blue,
        duration = StoryDto.Static.Duration.Short,
        order = 1,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    val story2 = StoryDto.Static(
        color = Color.Yellow,
        duration = StoryDto.Static.Duration.Short,
        order = 2,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    val story3 = StoryDto.Video(
        Uri.parse(VIDEO1),
        order = 3
    )
    val story4 = StoryDto.Static(
        color = Color.Gray,
        duration = StoryDto.Static.Duration.Short,
        order = 2,
        title = "Title Title Title Title Title Title Title Title Title Title Title "
    )
    storyList.add(story0)
    storyList.add(story1)
    storyList.add(story2)
    storyList.add(story3)
    storyList.add(story4)

    return StorySetDto(preview, storyList).toDomain()
}

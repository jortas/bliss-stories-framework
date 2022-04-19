package com.example.blissstories.models.mappers

import com.example.blissstories.models.api.StoryDto
import com.example.blissstories.models.api.StoryPreviewDto
import com.example.blissstories.models.api.StorySetDto
import com.example.blissstories.models.domain.Story
import com.example.blissstories.models.domain.StoryPreview
import com.example.blissstories.models.domain.StorySet

fun StoryDto.toDomain(): Story {
    return when (this) {
        is StoryDto.Static -> Story.Static(
            color,
            duration.toDomain(),
            title,
            description,
            order
        )
        is StoryDto.Video -> Story.Video(
            this.video,
            this.order
        )
    }
}

fun StoryDto.Static.Duration.toDomain(): Story.Static.Duration {
    return when (this) {
        StoryDto.Static.Duration.Short -> Story.Static.Duration.Short
        StoryDto.Static.Duration.Long -> Story.Static.Duration.Long
    }
}

fun StorySetDto.toDomain(): StorySet {
    return StorySet(
        preview.toDomain(),
        stories.map { it.toDomain() }
    )
}

fun StoryPreviewDto.toDomain(): StoryPreview {
    return StoryPreview(title, illustration, background, titleColor)
}

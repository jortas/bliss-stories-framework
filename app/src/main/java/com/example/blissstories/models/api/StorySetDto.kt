package com.example.blissstories.models.api

data class StorySetDto(
    val preview: StoryPreviewDto,
    val stories: List<StoryDto>
)
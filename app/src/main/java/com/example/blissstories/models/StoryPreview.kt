package com.example.blissstories.models

import com.example.blissstories.projectutils.resources.ColorKeyResource
import com.example.blissstories.projectutils.resources.ImageKeyResource
import com.example.blissstories.projectutils.resources.VisualResource

data class StoryPreview(
    val title: String,
    val illustration: ImageKeyResource?,
    val background: VisualResource,
    val titleColor: ColorKeyResource
)




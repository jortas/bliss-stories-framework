package com.example.blissstories.models.api

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.blissstories.projectutils.colorComposeKey
import com.example.blissstories.projectutils.colorKey
import com.example.blissstories.projectutils.resourceProvider
import com.example.blissstories.projectutils.resources.ColorKeyResource
import com.example.blissstories.projectutils.resources.ImageKeyResource
import com.example.blissstories.projectutils.resources.VisualResource

data class StoryPreviewDto(
    val title: String,
    val illustration: ImageKeyResource?,
    val background: VisualResource,
    val titleColor: ColorKeyResource
)
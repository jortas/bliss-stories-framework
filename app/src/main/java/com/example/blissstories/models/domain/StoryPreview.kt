package com.example.blissstories.models.domain

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.blissstories.projectutils.colorComposeKey
import com.example.blissstories.projectutils.colorKey
import com.example.blissstories.projectutils.resourceProvider
import com.example.blissstories.projectutils.resources.ColorKeyResource
import com.example.blissstories.projectutils.resources.ImageKeyResource
import com.example.blissstories.projectutils.resources.VisualResource

data class StoryPreview(
    val title: String,
    val illustration: ImageKeyResource?,
    val background: VisualResource,
    val titleColor: ColorKeyResource
) {
    @Composable
    fun backgroundColor(context: Context): Color {
        val defaultColor = Color.Black
        return if (background is ColorKeyResource) {
            context.resourceProvider.colorComposeKey(background) ?: defaultColor
        } else {
            defaultColor
        }
    }
}



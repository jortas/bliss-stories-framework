package com.example.blissstories.models.domain

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.blissstories.models.api.StoryDto

sealed class Story() {
    abstract val order: Int

    data class Video(val video: Uri, override val order: Int) : Story()

    data class Static(
        val color: Color,
        val duration: Duration,
        val title: String,
        val description: String? = null,
        override val order: Int
    ) : Story() {

        enum class Duration(val timeInMs: kotlin.Long) {
            Short(6000), Long(12000)
        }
    }
}
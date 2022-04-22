package com.example.blissstories.i9stories.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.blissstories.models.domain.StorySet

class StorySetsPlayerViewModel(
    val storySetsStateList: List<StorySet>,
    val initialStorySetIndex: Int
) : ViewModel()
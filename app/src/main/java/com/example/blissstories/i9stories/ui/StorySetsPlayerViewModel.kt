package com.example.blissstories.i9stories.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.blissstories.models.domain.StorySet

class StorySetsPlayerViewModel(
    val storySetsStateList: List<StorySet>,
    initialStorySetIndex: Int
) : ViewModel() {

    var currentStorySetIndex by mutableStateOf(initialStorySetIndex)
        private set

    private fun updateCurrentStorySetIndex(newCurrentStoryIndex: Int) {
        currentStorySetIndex = newCurrentStoryIndex
    }

    init {
        updateCurrentStorySetIndex(initialStorySetIndex)
    }

    fun goToNextStorySet() {
        updateCurrentStorySetIndex(currentStorySetIndex + 1)
    }

    fun goToPreviousStorySet() {
        updateCurrentStorySetIndex(currentStorySetIndex - 1)
    }
}
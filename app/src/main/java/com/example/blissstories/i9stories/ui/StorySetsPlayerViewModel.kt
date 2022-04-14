package com.example.blissstories.i9stories.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.blissstories.i9stories.ui.models.StorySetUiState

class StorySetsPlayerViewModel(
    storySetsStateList: List<StorySetUiState>,
    initialStorySetIndex: Int
) : ViewModel() {

    var currentStorySetIndex by mutableStateOf(initialStorySetIndex)
        private set

    val viewModels = storySetsStateList.map { StorySetPlayerViewModel(it) }

    private fun updateCurrentStorySetIndex(newCurrentStoryIndex: Int) {
        currentStorySetIndex = newCurrentStoryIndex
        viewModels.forEach {
            it.resetProgress()
            it.setPlaying(false)
        }
        viewModels[currentStorySetIndex].setPlaying(true)
    }

    val currentStoryViewModel
        get() = viewModels[currentStorySetIndex]

    fun goToNextStorySet() {
        updateCurrentStorySetIndex(currentStorySetIndex + 1)
    }

    fun goToPreviousStorySet() {
        updateCurrentStorySetIndex(currentStorySetIndex - 1)
    }
}
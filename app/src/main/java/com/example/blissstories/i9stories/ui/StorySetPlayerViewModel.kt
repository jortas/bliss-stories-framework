package com.example.blissstories.i9stories.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blissstories.i9stories.ui.models.StorySetUiState

class StorySetPlayerViewModel(storySetUiState: StorySetUiState) : ViewModel() {

    var state: StorySetUiState by mutableStateOf(storySetUiState, policy = neverEqualPolicy())

    fun setCurrentStoryIndex(newCurrentStoryIndex:Int) {
        state.currentStoryIndex = newCurrentStoryIndex
        state.resetProgress()
        state = state
    }

    fun goToPreviousStory() {
        setCurrentStoryIndex(state.currentStoryIndex - 1)
    }

    fun goToNextStory() {
        setCurrentStoryIndex(state.currentStoryIndex + 1)
    }

    fun setPlaying(playing: Boolean) {
        state.playing = playing
        state = state
    }

    fun resetProgress() {
        state.currentProgress = 0f
        state = state
    }

    fun setProgress(progress: Float) {
        state.currentProgress = progress
        state = state
    }
}
package com.example.blissstories.i9stories.ui.frames

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blissstories.models.domain.Story
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit

class StaticStoryPlayerViewModel() : ViewModel() {
    var elapsedPlayingTime = MutableStateFlow(0f)
    var startedPlayingTime = LocalDateTime.now()

    var duration: Long = 0L

    fun setClock(duration: Long) {
        elapsedPlayingTime.value = 0f
        startedPlayingTime = LocalDateTime.now()
        this.duration = duration
    }

    private lateinit var clockJob: Job
    fun resumeClock() {
        startedPlayingTime = LocalDateTime.now()
        clockJob = startClock(duration)
    }

    fun pauseClock() {
        clockJob.cancel()
    }

    private fun startClock(duration: Long): Job {
        return viewModelScope.launch {
            val savedElapsedTime = elapsedPlayingTime.value
            while (isActive && elapsedPlayingTime.value < duration) {
                val elapsedTime = startedPlayingTime.until(LocalDateTime.now(), ChronoUnit.MILLIS)
                elapsedPlayingTime.value = savedElapsedTime + elapsedTime
                delay(20)
            }
            if (elapsedPlayingTime.value >= duration){
                //onStoryFinished()
            }
        }
    }
}
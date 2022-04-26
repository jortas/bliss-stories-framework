package com.example.blissstories.i9stories.ui.frames

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit

class StaticStoryPlayerViewModel() : ViewModel() {
    var elapsedPlayingTime = MutableStateFlow(0f)
    private var startedPlayingTime = LocalDateTime.now()
    val channel = Channel<Boolean>()

    var duration: Long = 0L

    fun setClock(duration: Long) {
        elapsedPlayingTime.value = 0f
        startedPlayingTime = LocalDateTime.now()
        this.duration = duration
    }

    private var clockJob: Job? = null
    fun resumeClock() {
        startedPlayingTime = LocalDateTime.now()
        if (clockJob?.isActive != true) {
            clockJob = startClock(duration)
        }
    }

    fun pauseClock() {
        clockJob?.cancel()
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
                channel.send(true)
            }
        }
    }
}
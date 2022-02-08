package com.example.moeyslider.i9stories

import android.util.Log
import androidx.compose.foundation.gestures.GestureCancellationException
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.moeyslider.models.Story
import com.example.moeyslider.models.storyFactoryMock
import java.lang.RuntimeException
import kotlin.math.ln
import kotlin.math.log
import kotlin.math.tanh

@Composable
fun StoryFramework(
    modifier: Modifier,
    storySet: List<Story>,
    backStorySetAction: () -> Unit,
    finishedStorySetAction: () -> Unit
) {
    BoxWithConstraints(modifier) {
        val constraintScope = this

        var currentStoryIndex by remember { mutableStateOf(0) }
        var currentVideoProgress by remember { mutableStateOf(0.0f) }

        var totalDragAmount by remember { mutableStateOf(100f) }

        var playerState by remember { mutableStateOf(VideoPlayerState.Playing) }

        val newModifier = remember(modifier) {
            modifier
                .pointerInput(Unit) {
                    this.forEachGesture {
                        awaitPointerEventScope {
                            this.
                        }

                    }
                    detectVerticalDragGestures(
                        onDragStart = { totalDragAmount = 0f },
                        onDragEnd = { totalDragAmount = 0f }
                    )
                    { change, dragAmount ->
                        totalDragAmount = kotlin.math.max(totalDragAmount + dragAmount, 0f)

                        Log.d("JORTAS", "$change && $dragAmount")
                    }
                    detectTapGestures(
                        onPress = {
                            playerState = VideoPlayerState.Paused
                            try {
                                val pressStartTime = System.currentTimeMillis()
                                awaitRelease()
                                val pressEndTime = System.currentTimeMillis()
                                val totalPressTime = pressEndTime - pressStartTime
                                val tapType = getTapType(
                                    pressedTime = totalPressTime,
                                    tapPosition = it,
                                    width = constraintScope.maxWidth.toPx()
                                )
                                when (tapType) {
                                    TapType.ShortLeft -> {
                                        if (currentStoryIndex == 0) {
                                            backStorySetAction()
                                        } else {
                                            currentStoryIndex--
                                            currentVideoProgress = 0f
                                        }
                                    }
                                    TapType.ShortCenter,
                                    TapType.ShortRight -> {
                                        if (currentStoryIndex == storySet.size) {
                                            backStorySetAction()
                                        } else {
                                            currentStoryIndex++
                                            currentVideoProgress = 0f
                                        }
                                    }
                                    TapType.Long -> 1 + 1;//TODO()
                                }
                                playerState = VideoPlayerState.Playing
                            } catch (e: GestureCancellationException) { //Motion was used
                                playerState = VideoPlayerState.Playing
                            }
                        }
                    )
                }
        }

        val totalDragAmountDp: Dp
        val proportion = remember(maxHeight, maxWidth) { maxHeight/maxWidth }

        with(LocalDensity.current) {
            totalDragAmountDp = remember(totalDragAmount) {
                val x = totalDragAmount.toDp().value.toFloat()
                (tanh(x / (maxHeight.value - maxHeight.value/Math.E)) * (maxHeight.value - maxHeight.value/1.612f)).dp
            }
        }

        Box(
            newModifier
                .padding(
                    top = totalDragAmountDp,
                    start = totalDragAmountDp / proportion /20,
                    end = totalDragAmountDp / proportion/20
                )
                .defaultMinSize(maxWidth, maxHeight)

        ) {
            ComposedStoryProgressBar(
                modifier = Modifier
                    .zIndex(2f)
                    .padding(2.dp),
                numberOfStories = storySet.size,
                currentVideoIndex = currentStoryIndex,
                progressOfCurrentVideo = currentVideoProgress
            )

            VideoPlayer(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f),
                state = playerState,
                currentVideoIndex = currentStoryIndex,
                onStateChange = { newState -> playerState = newState },
                onVideoIndexChange = {
                    currentStoryIndex = it
                    currentVideoProgress = 0f
                },
                onVideoProgressChange = {
                    currentVideoProgress = it
                },
                videoLinks = storySet.map { it.video.link },
            )
        }
    }
}


private fun getTapType(
    pressedTime: Long,
    tapPosition: Offset,
    width: Float
): TapType {
    val quarterOfWidth = width / 4f
    return if (pressedTime < MAX_TIME_TAP_MS) {
        return when (tapPosition.x) {
            in 0f..quarterOfWidth -> TapType.ShortLeft
            in quarterOfWidth..(width - quarterOfWidth) -> TapType.ShortCenter
            in (width - quarterOfWidth)..width -> TapType.ShortRight
            else -> throw RuntimeException("Something went wrong detecting tap type, this state should be impossible")
        }
    } else {
        TapType.Long
    }
}

@Preview
@Composable
private fun StoryFrameworkPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize()) {
            StoryFramework(
                modifier = Modifier,
                storySet = storyFactoryMock(), {}, {}
            )

        }
    }
}

private const val MAX_TIME_TAP_MS = 200

enum class TapType {
    ShortLeft,
    ShortCenter,
    ShortRight,

    Long
}
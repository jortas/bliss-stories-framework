package com.example.moeyslider.i9stories

import android.util.Log
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.moeyslider.models.Story
import com.example.moeyslider.models.storyFactoryMock
import com.google.common.primitives.Floats.max
import kotlinx.coroutines.NonCancellable.children
import java.lang.RuntimeException
import kotlin.math.abs
import kotlin.math.tanh

@Composable
fun StoryFramework(
    modifier: Modifier,
    storySet: List<Story>,
    backStorySetAction: () -> Unit,
    finishedStorySetAction: () -> Unit,
    dismissStories: () -> Unit = {}
) {
    var currentStoryIndex by remember { mutableStateOf(0) }
    var currentVideoProgress by remember { mutableStateOf(0.0f) }

    var totalVerticalDragAmount by remember { mutableStateOf(0f) }

    var playerState by remember { mutableStateOf(VideoPlayerState.Playing) }
    var tapEvent: Offset? by remember { mutableStateOf(null) }

    BoxWithConstraints(modifier) {
        val constraintScope = this
        val localDensity = LocalDensity.current

        val newModifier = remember() {
            modifier.addMultipleGestures(
                onGestureStart = {
                    playerState = VideoPlayerState.Paused
                    totalVerticalDragAmount = 0f
                },
                onGestureEnd = { playerState = VideoPlayerState.Playing },
                onPress = { tapEvent = it },
                onVerticalDrag = { totalVerticalDragAmount = max(0f, it) },
                onVerticalDragEnd = {
                    with(localDensity) {
                        if (totalVerticalDragAmount > maxHeight.toPx() * PERCENTAGE_TO_DISMISS){

                        }
                            totalVerticalDragAmount = 0f
                        dismissStories()
                    }
                }
            )
        }
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(
                durationMillis = getAnimationSpeed(progress),
                easing = FastOutLinearInEasing
            )

        currentStoryIndex = remember(tapEvent) {
            val tapType = tapEvent?.let {
                with(localDensity) {
                    getTapType(
                        tapPosition = it,
                        width = constraintScope.maxWidth.toPx() //todo to px
                    )
                }
            }
            when (tapType) {
                TapType.ShortLeft -> {
                    if (currentStoryIndex == 0) {
                        backStorySetAction()
                    } else {
                        currentVideoProgress = 0f
                        return@remember currentStoryIndex - 1
                    }
                }
                TapType.ShortCenter,
                TapType.ShortRight -> {
                    if (currentStoryIndex == storySet.lastIndex) {
                        backStorySetAction()
                    } else {
                        currentVideoProgress = 0f
                        return@remember currentStoryIndex + 1
                    }
                }
                else -> {}//Do nothing
            }
            return@remember currentStoryIndex
        }
        val proportion = remember(maxHeight, maxWidth) { maxHeight / maxWidth }

        val totalDragAmountDp = remember(totalVerticalDragAmountDp) {
            val x = with(localDensity) { totalVerticalDragAmount.toDp().value }
            (tanh(x / (maxHeight.value - maxHeight.value / GOLD_RATIO)) * (maxHeight.value - maxHeight.value / GOLD_RATIO)).dp
        }
        val paddingAmount = remember(totalDragAmountDp) {
            totalDragAmountDp / PADDING_PROPORTION
        }

        Box(
            newModifier
                .offset(0.dp, y = totalDragAmountDp)
                .padding(
                    start = paddingAmount / proportion / 2,
                    end = paddingAmount / proportion / 2,
                    bottom = paddingAmount
                )
                .clip(RoundedCornerShape(totalDragAmountDp / CORNER_RADIUS_PROPORTION))


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

private fun Modifier.addMultipleGestures(
    onGestureStart: () -> Unit = {},
    onGestureEnd: () -> Unit = {},
    onPress: (Offset) -> Unit = {},
    onLongPress: (Offset) -> Unit = {},
    onVerticalDrag: (Float) -> Unit = {},
    onVerticalDragEnd: () -> Unit = {},
    onHorizontalDrag: (Float) -> Unit = {},
    onHorizontalDragEnd: () -> Unit = {}
): Modifier {
    return this
        .pointerInput(Unit) {
            this.forEachGesture {
                awaitPointerEventScope {
                    val firstTouchPointer = awaitFirstDown(requireUnconsumed = false)
                    onGestureStart()
                    val pressStartTime = System.currentTimeMillis()
                    var pointer: PointerInputChange?
                    var drag = Offset.Zero
                    var totalDragWithDirection: Float

                    do {
                        pointer = awaitDragOrCancellation(firstTouchPointer.id)
                        pointer?.let {
                            drag += it.positionChangeIgnoreConsumed()
                        }
                    } while (abs(drag.x) < PRESS_SAFE_ZONE && abs(drag.y) < PRESS_SAFE_ZONE && pointer != null)

                    val pressedTime = System.currentTimeMillis() - pressStartTime
                    if (pointer == null && pressedTime < MAX_TIME_TAP_MS) {
                        onPress(firstTouchPointer.position)
                    } else if (pointer == null && pressedTime > MAX_TIME_TAP_MS) {
                        onLongPress(firstTouchPointer.position)
                    } else if (pointer != null && abs(drag.x) < abs(drag.y)) {
                        totalDragWithDirection = drag.y
                        onVerticalDrag(totalDragWithDirection)
                        do {
                            pointer = awaitVerticalDragOrCancellation(firstTouchPointer.id)
                            pointer?.let {
                                totalDragWithDirection += it.positionChange().y
                                onVerticalDrag(totalDragWithDirection)
                            }
                        } while (pointer != null)
                        onVerticalDragEnd()
                    } else if (pointer != null && abs(drag.x) >= abs(drag.y)) {
                        totalDragWithDirection = drag.x
                        onHorizontalDrag(totalDragWithDirection)
                        do {
                            pointer = awaitVerticalDragOrCancellation(firstTouchPointer.id)
                            pointer?.let {
                                totalDragWithDirection += it.positionChange().x
                                onHorizontalDrag(totalDragWithDirection)
                            }
                        } while (pointer != null)
                        onHorizontalDragEnd()
                    }
                    onGestureEnd()
                }
            }
        }
}

private fun getTapType(
    tapPosition: Offset,
    width: Float
): TapType {
    val quarterOfWidth = width / 4f
    return when (tapPosition.x) {
        in 0f..quarterOfWidth -> TapType.ShortLeft
        in quarterOfWidth..(width - quarterOfWidth) -> TapType.ShortCenter
        in (width - quarterOfWidth)..width -> TapType.ShortRight
        else -> throw RuntimeException("Something went wrong detecting tap type, this state should be impossible")
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

private const val PRESS_SAFE_ZONE = 30f
private const val MAX_TIME_TAP_MS = 200
private const val PERCENTAGE_TO_DISMISS = 0.25f
private const val GOLD_RATIO = 1.612f
private const val PADDING_PROPORTION = GOLD_RATIO * 4
private const val CORNER_RADIUS_PROPORTION = GOLD_RATIO * 8

enum class TapType {
    ShortLeft,
    ShortCenter,
    ShortRight,

    Long
}
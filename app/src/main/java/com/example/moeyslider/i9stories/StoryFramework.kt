package com.example.moeyslider.i9stories

import android.util.Log
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.moeyslider.models.Story
import com.example.moeyslider.utills.toDpSize
import com.example.moeyslider.utills.toPx
import com.google.common.primitives.Floats.max
import java.lang.RuntimeException
import kotlin.math.abs
import kotlin.math.tanh

@Composable
fun StoryFramework(
    modifier: Modifier,
    initialShape: Shape,
    initialSize: Size,
    storySet: List<Story>,
    close: () -> Unit,
    finishedStorySetAction: () -> Unit,
    dismissStories: () -> Unit = {}
) {
    var justLaunched by remember { mutableStateOf(true) }
    var closeEvent by remember { mutableStateOf(false) }
    var gestureEndEvent by remember { mutableStateOf(false) }

    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val size by animateSizeAsState(
            targetValue = if (justLaunched || closeEvent) initialSize else {
                Size(
                    maxWidth.value,
                    maxHeight.value
                )
            }, finishedListener = { size ->
                if (closeEvent) {
                    close()
                }
            })
        val sizeDp = size.toDpSize()
        val sizePx = sizeDp.toPx(LocalDensity.current)


        LaunchedEffect("init") {
            justLaunched = false
        }

        LaunchedEffect(gestureEndEvent) {
            if (gestureEndEvent) {
                closeEvent = true
            }
        }

        var currentStoryIndex by remember { mutableStateOf(0) }
        var currentVideoProgress by remember { mutableStateOf(0.0f) }

        var totalVerticalDragAmount by remember { mutableStateOf(0f) }

        var playerState by remember { mutableStateOf(VideoPlayerState.Playing) }
        var tapEvent: Offset? by remember { mutableStateOf(null) }
        val tapType = getTapType(
            tapPosition = tapEvent,
            width = sizePx.width
        )

        LaunchedEffect(key1 = tapType) {
            when (tapType) {
                TapType.ShortLeft -> {
                    if (currentStoryIndex == 0) {
                        closeEvent = true
                    } else {
                        currentVideoProgress = 0f
                        currentStoryIndex -= 1
                    }
                }
                TapType.ShortCenter,
                TapType.ShortRight -> {
                    if (currentStoryIndex == storySet.lastIndex) {
                        closeEvent = true
                    } else {
                        currentVideoProgress = 0f
                        currentStoryIndex += 1
                    }
                }
                TapType.None,
                TapType.Long -> {
                    //Nothing
                }
            }
        }

        Box(Modifier.size(sizeDp)) {

            val proportion = remember(sizeDp) { sizeDp.height / sizeDp.width }

            val totalDragAmountDp = remember(totalVerticalDragAmount) {
                val x = totalVerticalDragAmount//.toDp().value.toFloat()
                (tanh(x / (size.height - size.height / Math.E)) * (size.height - size.height / 1.612f)).dp
            }

            val newModifier = remember {
                Modifier.addMultipleGestures(
                    onGestureStart = {
                        playerState = VideoPlayerState.Paused
                        totalVerticalDragAmount = 0f
                    },
                    onGestureEnd = {
                        playerState = VideoPlayerState.Playing
                        gestureEndEvent = true
                    },
                    onPress = { tapEvent = it },
                    onVerticalDrag = { totalVerticalDragAmount = max(0f, it) },
                    onVerticalDragEnd = {
                        totalVerticalDragAmount = 0f
                        dismissStories()
                    }
                )
            }
            Box(
                newModifier
                    .padding(
                        top = totalDragAmountDp,
                        start = totalDragAmountDp / proportion / 20,
                        end = totalDragAmountDp / proportion / 20
                    )

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
    tapPosition: Offset?,
    width: Float
): TapType {
    if (tapPosition == null) {
        return TapType.None
    }
    val quarterOfWidth = width / 4f
    return when (tapPosition.x) {
        in 0f..quarterOfWidth -> TapType.ShortLeft
        in quarterOfWidth..(width - quarterOfWidth) -> TapType.ShortCenter
        in (width - quarterOfWidth)..width -> TapType.ShortRight
        else -> throw RuntimeException("Something went wrong detecting tap type, this state should be impossible")
    }
}

private const val PRESS_SAFE_ZONE = 30f
private const val MAX_TIME_TAP_MS = 200

enum class TapType {
    None,
    ShortLeft,
    ShortCenter,
    ShortRight,
    Long
}
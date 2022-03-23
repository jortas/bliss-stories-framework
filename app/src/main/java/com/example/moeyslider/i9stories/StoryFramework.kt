package com.example.moeyslider.i9stories

import android.util.Log
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.moeyslider.models.Story
import com.example.moeyslider.utills.toDpSize
import com.example.moeyslider.utills.toPx
import com.google.common.primitives.Floats.max
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.tanh

@Composable
fun StoryFramework(
    modifier: Modifier,
    initialShape: Shape,
    initialSize: Size,
    storySet: List<Story>,
    close: () -> Unit,
    finishedStorySetAction: () -> Unit
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
        val sizeDp = remember(size) {
            size.toDpSize()
        }
        val density = LocalDensity.current
        val sizePx = remember(sizeDp) {
            sizeDp.toPx(density)
        }

        val maxHeightPx = remember(maxHeight) {
            with(density) {
                maxHeight.toPx()
            }
        }

        LaunchedEffect("init") {
            justLaunched = false
        }

        var currentStoryIndex by remember { mutableStateOf(0) }
        var currentVideoProgress by remember { mutableStateOf(0.0f) }

        var totalVerticalDragAmount by remember { mutableStateOf(0f) }

        var playerState by remember { mutableStateOf(VideoPlayerState.Playing) }
        var tapEvent: Offset? by remember { mutableStateOf(null) }
        val tapType = remember(tapEvent) {
            getTapType(
                tapPosition = tapEvent,
                width = sizePx.width
            )
        }

        LaunchedEffect(gestureEndEvent) {
            val x = totalVerticalDragAmount.absoluteValue
            Log.d("YOYO", "$x")
            if (gestureEndEvent && totalVerticalDragAmount > maxHeightPx * PERCENTAGE_TO_DISMISS) {
                closeEvent = true
            }
            gestureEndEvent = false
        }

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


        val proportion = remember(sizeDp) { sizeDp.height / sizeDp.width }

        val topOffset = remember(totalVerticalDragAmount) {
            val x = totalVerticalDragAmount
            with(density){
                (tanh(x / (maxHeightPx - maxHeightPx / GOLD_RATIO)) * (maxHeightPx - maxHeightPx / GOLD_RATIO)).toDp()
            }
        }

        val paddingAmount = remember(topOffset) {
            topOffset / PADDING_PROPORTION
        }

        val gesturesModifier = remember() {
            Modifier
                .addMultipleGestures(
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
                        gestureEndEvent = true
                    }
                )
        }

        val newModifier = remember(gesturesModifier, sizeDp) {
            gesturesModifier.size(sizeDp)
        }

        Box(
            newModifier
                .offset(0.dp, y = topOffset)
                .padding(
                    start = paddingAmount / proportion / 2,
                    end = paddingAmount / proportion / 2,
                    bottom = paddingAmount
                )
                .clip(RoundedCornerShape(topOffset / CORNER_RADIUS_PROPORTION))


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
        else -> TapType.None
    }
}

private const val PRESS_SAFE_ZONE = 30f
private const val MAX_TIME_TAP_MS = 200
private const val PERCENTAGE_TO_DISMISS = 0.25f
private const val GOLD_RATIO = 1.612f
private const val PADDING_PROPORTION = GOLD_RATIO * 4
private const val CORNER_RADIUS_PROPORTION = GOLD_RATIO * 8

enum class TapType {
    None,
    ShortLeft,
    ShortCenter,
    ShortRight,
    Long
}
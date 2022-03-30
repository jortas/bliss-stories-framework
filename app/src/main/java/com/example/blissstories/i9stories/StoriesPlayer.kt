package com.example.blissstories.i9stories

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import com.example.blissstories.models.Story
import com.example.blissstories.utills.toDpSize
import com.example.blissstories.utills.toPx
import com.google.android.exoplayer2.MediaItem
import kotlin.math.abs
import kotlin.math.tanh

@Composable
fun StoriesPlayer(
    modifier: Modifier,
    initialShape: Shape = RoundedCornerShape(4.dp),
    initialSize: Size = Size(1f, 1f),
    animateEntry: Boolean = true,
    storySet: List<Story>,
    close: () -> Unit,
    onHorizontalDrag: (Dp) -> Unit,
    onHorizontalDragEnd: () -> Unit,
    onFinishedStorySet: () -> Unit = {},

    ) {
    val context = LocalContext.current
    val density = LocalDensity.current

    var justLaunched by remember { mutableStateOf(true) }
    var closeEvent by remember { mutableStateOf(false) }

    val mediaItems = remember(storySet) {
        storySet.filterIsInstance(Story.Video::class.java).map { MediaItem.fromUri(it.video) }
    }

    //This val corresponds to the index of the video the exoplayer should be in
    val mediaItemsIndex = remember(storySet) {
        var totalVideos = 0
        storySet.map { if (it is Story.Video && totalVideos < mediaItems.size - 1) totalVideos++ else totalVideos }
    }

    var currentStoryIndex by remember { mutableStateOf(0) }
    val currentVideoIndex = remember(currentStoryIndex, mediaItemsIndex) {
        mediaItemsIndex[currentStoryIndex]
    }
    var currentStoryProgress by remember { mutableStateOf(0.0f) }

    var playerState by remember { mutableStateOf(StoryFrameState.Playing) }

    val exoPlayer = remember {
        val exoPlayer = ExoPlayerCreator.createExoPlayer(
            context,
            mediaItems,
            onStateChange = { newState -> playerState = newState },
            onVideoChange = {
                currentStoryIndex = it
                currentStoryProgress = 0f
            }
        )
        exoPlayer.pauseAtEndOfMediaItems = true
        exoPlayer
    }

    LaunchedEffect("init") {
        justLaunched = false
    }

    LaunchedEffect(currentStoryIndex) {
        playerState = StoryFrameState.Playing
    }

    fun onPress(tapEvent: Offset, width: Float) {
        val tapType = getTapType(
            tapPosition = tapEvent,
            width = width
        )

        when (tapType) {
            TapType.ShortLeft -> {
                if (currentStoryIndex == 0) {
                    closeEvent = true
                } else {
                    currentStoryProgress = 0f
                    currentStoryIndex -= 1
                }
            }
            TapType.ShortCenter,
            TapType.ShortRight -> {
                if (currentStoryIndex == storySet.lastIndex) {
                    closeEvent = true
                } else {
                    currentStoryProgress = 0f
                    currentStoryIndex += 1
                }
            }
            TapType.None,
            TapType.Long -> {
                //Nothing
            }
        }
    }

    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val maxSize = remember(maxWidth, maxHeight) {
            Size(
                maxWidth.value,
                maxHeight.value
            )
        }

        val size by animateSizeAsState(
            targetValue = if (justLaunched && animateEntry || closeEvent) initialSize else {
                maxSize
            }, finishedListener = { size ->
                if (closeEvent) {
                    close()
                }
            })

        val sizeDp = remember(size) {
            size.toDpSize()
        }

        val sizePx = remember(sizeDp) {
            sizeDp.toPx(density)
        }
        val proportion = remember(sizeDp) { sizeDp.height / sizeDp.width }

        var totalVerticalDragAmount by remember { mutableStateOf(0.dp) }


        val topOffset = remember(totalVerticalDragAmount) {
            val x = totalVerticalDragAmount
            with(density) {
                (tanh(x / (maxHeight - maxHeight / GOLD_RATIO)) * (maxHeight - maxHeight / GOLD_RATIO).value).toDp()
            }
        }

        val paddingAmount = remember(topOffset) {
            topOffset / PADDING_PROPORTION
        }

        val gesturesModifier =
            Modifier
                .addMultipleGestures(
                    density,
                    onGestureStart = {
                        playerState = StoryFrameState.Paused
                        totalVerticalDragAmount = 0.dp
                    },
                    onGestureEnd = {
                        playerState = StoryFrameState.Playing
                    },
                    onPress = { onPress(it, sizePx.width) },
                    onVerticalDrag = { totalVerticalDragAmount = max(0.dp, it) },
                    onVerticalDragEnd = {
                        if (totalVerticalDragAmount > maxHeight * PERCENTAGE_TO_DISMISS) {
                            closeEvent = true
                        }
                        totalVerticalDragAmount = 0.dp
                    },
                    onHorizontalDrag = onHorizontalDrag,
                    onHorizontalDragEnd = onHorizontalDragEnd
                )


        val newModifier = remember(gesturesModifier, sizeDp) {
            gesturesModifier.size(sizeDp)
        }


        Box(
            newModifier
                .offset(x = 0.dp, y = topOffset)
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
                currentStoryIndex = currentStoryIndex,
                progressOfCurrentStory = currentStoryProgress
            )

            when (val currentStory = storySet[currentStoryIndex]) {
                is Story.Video -> VideoStoryFrame(
                    modifier = Modifier
                        .size(sizeDp)
                        .zIndex(1f),
                    exoPlayer = exoPlayer,
                    playerState = playerState,
                    currentVideoIndex = currentVideoIndex,
                    onStoryProgressChange = {
                        currentStoryProgress = it
                    },
                    onStoryFinished = { currentStoryIndex++ },
                )
                is Story.Static -> StaticStoryPlayer(
                    story = currentStory,
                    playerState = playerState,
                    onStoryProgressChange = {
                        currentStoryProgress = it
                    },
                    onStoryFinished = { currentStoryIndex++ }
                )
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

private const val PERCENTAGE_TO_DISMISS = 0.25f
private const val GOLD_RATIO = 1.612f
private const val PADDING_PROPORTION = GOLD_RATIO * 4
private const val CORNER_RADIUS_PROPORTION = GOLD_RATIO * 8

enum class StoryFrameState() {
    Playing, Paused, Unknown
}

enum class TapType {
    None,
    ShortLeft,
    ShortCenter,
    ShortRight,
    Long
}
package com.example.blissstories.i9stories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import com.example.blissstories.i9stories.components.ComposedStoryProgressBar
import com.example.blissstories.i9stories.frames.StaticStoryPlayer
import com.example.blissstories.i9stories.frames.VideoStoryFrame
import com.example.blissstories.i9stories.utils.ExoPlayerCreator
import com.example.blissstories.models.Story
import com.example.blissstories.models.StorySet
import com.example.blissstories.utills.hyperbolicTangentInterpolator
import com.example.blissstories.utills.toDpSize
import com.example.blissstories.utills.toPx
import com.google.android.exoplayer2.MediaItem

@Composable
fun StoriesPlayer(
    modifier: Modifier,
    cornerRadius: Dp,
    fractionOfSize: Float,
    storySet: StorySet?,
    close: () -> Unit,
    onHorizontalDrag: (Dp) -> Unit,
    onHorizontalDragEnd: () -> Unit,
    onFinishedStorySet: () -> Unit = {},

    ) {
    val context = LocalContext.current
    val density = LocalDensity.current

    if (storySet == null) {
        return
    }

    val videoMediaItems = remember(storySet) {
        storySet.filterIsInstance(Story.Video::class.java).map { MediaItem.fromUri(it.video) }
    }

    //This val corresponds to the index of the video the exoplayer should be in
    val mediaItemsIndex = remember(storySet) {
        var nextIndex = 0
        var currentIndex = 0
        storySet.map {
            currentIndex = nextIndex
            if (it is Story.Video && currentIndex < videoMediaItems.size - 1) {
                nextIndex++
                currentIndex
            } else {
                currentIndex
            }
        }
    }

    var currentStoryIndex by remember { mutableStateOf(0) }
    val currentStory = remember(currentStoryIndex) {
        storySet[currentStoryIndex] }
    val currentVideoIndex = remember(currentStoryIndex, mediaItemsIndex) {
        mediaItemsIndex[currentStoryIndex]
    }
    var currentStoryProgress by remember { mutableStateOf(0.0f) }

    var playerState by remember { mutableStateOf(StoryFrameState.Playing) }

    val exoPlayer = remember {
        val exoPlayer = ExoPlayerCreator.createExoPlayer(
            context,
            videoMediaItems,
            onStateChange = { newState -> playerState = newState },
            onVideoChange = {
                currentStoryIndex = it
                currentStoryProgress = 0f
            }
        )
        exoPlayer.pauseAtEndOfMediaItems = true
        exoPlayer
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
                    close()
                } else {
                    currentStoryProgress = 0f
                    currentStoryIndex -= 1
                }
            }
            TapType.ShortCenter,
            TapType.ShortRight -> {
                if (currentStoryIndex == storySet.lastIndex) {
                    onFinishedStorySet()
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
        val size = remember(maxWidth, maxHeight) {
            Size(
                maxWidth.value,
                maxHeight.value
            )
        }
        val sizeDp = remember(size) { size.toDpSize() }
        val sizePx = remember(sizeDp) { sizeDp.toPx(density) }
        val proportion = remember(sizeDp) { sizeDp.height / sizeDp.width }

        var totalVerticalDragAmount by remember { mutableStateOf(0.dp) }

        val limitVerticalDrag = remember(maxHeight) {
            maxHeight.value - maxHeight.value / GOLD_RATIO
        }
        val topOffset = remember(totalVerticalDragAmount) {
            hyperbolicTangentInterpolator(
                totalVerticalDragAmount.value,
                0f,
                limitVerticalDrag
            ).dp
        }

        val scale = remember(topOffset) {
            val percentage = topOffset.value / limitVerticalDrag
            1f - (1f - MIN_SIZE_FRACTION_IN_HORIZONTAL_TRANSITION) * percentage
        }

        val radius = remember(topOffset) {
            val percentage = topOffset.value / limitVerticalDrag
            (1f - (1f - MAX_RADIUS_IN_HORIZONTAL_TRANSITION) * percentage).dp
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
                            close()
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
                .scale(fractionOfSize * scale)
                .clip(RoundedCornerShape(cornerRadius + radius))


        ) {
            ComposedStoryProgressBar(
                modifier = Modifier
                    .zIndex(2f)
                    .padding(16.dp),
                numberOfStories = storySet.size,
                currentStoryIndex = currentStoryIndex,
                currentStoryProgress = currentStoryProgress,
            )

            when (currentStory) {
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
                    onStoryFinished = { currentStoryIndex++ },
                    animateFixedItems = currentStoryIndex == 0
                )
            }
        }
    }
}

enum class StoryFrameState() {
    Playing, Paused, Unknown
}

fun StoryFrameState.isPlaying(): Boolean {
    return this == StoryFrameState.Playing
}

private const val PERCENTAGE_TO_DISMISS = 0.25f
const val GOLD_RATIO = 1.612f
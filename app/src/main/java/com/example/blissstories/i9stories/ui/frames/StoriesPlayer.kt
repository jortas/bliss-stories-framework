package com.example.blissstories.i9stories.ui.frames

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.zIndex
import com.example.blissstories.i9stories.ui.*
import com.example.blissstories.i9stories.ui.ExoPlayerCreator
import com.example.blissstories.i9stories.ui.MAX_RADIUS_IN_HORIZONTAL_TRANSITION
import com.example.blissstories.i9stories.ui.addMultipleGestures
import com.example.blissstories.i9stories.ui.components.ProgressBar
import com.example.blissstories.i9stories.ui.getTapType
import com.example.blissstories.models.domain.Story
import com.example.blissstories.models.domain.StorySet
import com.example.blissstories.utills.hyperbolicTangentInterpolator
import com.example.blissstories.utills.toDpSize
import com.example.blissstories.utills.toPx

@Composable
fun StoriesPlayer(
    modifier: Modifier,
    cornerRadius: Dp,
    fractionOfSize: Float,
    storySet: StorySet,
    onFocus: Boolean,
    initialCurrentStoryIndex: Int,
    updateCurrentStoryIndex: (Int) -> Unit,
    close: () -> Unit,
    onHorizontalDrag: (Dp) -> Unit,
    onHorizontalDragEnd: () -> Unit,
    onFinishedStorySet: () -> Unit = {},
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    var currentStoryIndex by remember(initialCurrentStoryIndex) {
        mutableStateOf(initialCurrentStoryIndex)
    }
    var progress by remember(currentStoryIndex, onFocus) {
        mutableStateOf(0f)
    }
    var playing by remember(onFocus) {
        mutableStateOf(onFocus)
    }

    val playerState = remember(playing) {
        if (playing) {
            StoryFrameState.Playing
        } else {
            StoryFrameState.Paused
        }
    }

    val exoPlayer = remember {
        val exoPlayer = ExoPlayerCreator.createExoPlayer(
            context,
            storySet.videoMediaItems,
            onPlayingChange = {
                if (storySet.currentStory is Story.Video) {
                    playing = it
                }
            },
            onVideoChange = { updateCurrentStoryIndex(it) }
        )
        exoPlayer.pauseAtEndOfMediaItems = true
        exoPlayer
    }

    LaunchedEffect(playing) {
        exoPlayer.playWhenReady = playing
    }

    LaunchedEffect(playing, progress) {
        if (storySet.currentStory is Story.Video) {
            progress = exoPlayer.currentProgress()
        }
    }

    fun onPress(tapEvent: Offset, width: Float) {
        val tapType = getTapType(
            tapPosition = tapEvent,
            width = width
        )

        when (tapType) {
            TapType.ShortLeft -> {
                if (storySet.isInFirstStory()) {
                    close()
                } else {
                    currentStoryIndex--
                    updateCurrentStoryIndex(currentStoryIndex)
                }
            }
            TapType.ShortCenter,
            TapType.ShortRight -> {
                if (storySet.isInLastStory()) {
                    onFinishedStorySet()
                } else {
                    currentStoryIndex++
                    updateCurrentStoryIndex(currentStoryIndex)
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
                        playing = false
                        totalVerticalDragAmount = 0.dp
                    },
                    onGestureEnd = {
                        playing = true
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

            ProgressBar(
                modifier = Modifier
                    .zIndex(2f)
                    .padding(16.dp),
                numberOfSteps = storySet.stories.size,
                currentStep = storySet.currentStoryIndex,
                stepProgress = progress,
            )

            when (storySet.currentStory) {
                is Story.Video -> VideoStoryFrame(
                    modifier = Modifier
                        .size(sizeDp)
                        .zIndex(1f),
                    exoPlayer = exoPlayer,
                    currentVideoIndex = storySet.currentVideoIndex,
                    onFocus = onFocus,
                    onStoryProgressChange = {
                        progress = it
                    },
                    onStoryFinished = onStoryFinished(
                        storySet,
                        currentStoryIndex,
                        {
                            currentStoryIndex = it
                            updateCurrentStoryIndex(it)
                        },
                        onFinishedStorySet
                    ),
                )
                is Story.Static -> StaticStoryPlayer(
                    story = storySet.currentStaticStory,
                    playerState = playerState,
                    onStoryProgressChange = { progress = it },
                    onStoryFinished = onStoryFinished(
                        storySet,
                        currentStoryIndex, {
                            currentStoryIndex = it
                            updateCurrentStoryIndex(it)
                        },
                        onFinishedStorySet
                    ),
                    animateFixedItems = storySet.isInFirstStory()
                )
            }
        }
    }
}

private fun onStoryFinished(
    storySet: StorySet,
    currentStoryIndex: Int,
    updateCurrentStoryIndex: (Int) -> Unit,
    onFinishedstorySet: () -> Unit
): () -> Unit = {
    if (storySet.stories.lastIndex == currentStoryIndex) {
        onFinishedstorySet()
    } else {
        updateCurrentStoryIndex(currentStoryIndex + 1)
    }
}

enum class StoryFrameState() {
    Playing, Paused
}

fun StoryFrameState.isPlaying(): Boolean {
    return this == StoryFrameState.Playing
}

private const val PERCENTAGE_TO_DISMISS = 0.25f
const val GOLD_RATIO = 1.612f
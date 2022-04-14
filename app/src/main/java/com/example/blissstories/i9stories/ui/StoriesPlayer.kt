package com.example.blissstories.i9stories.ui

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
import com.example.blissstories.i9stories.ui.components.ComposedStoryProgressBar
import com.example.blissstories.i9stories.ui.frames.StaticStoryPlayer
import com.example.blissstories.i9stories.ui.frames.VideoStoryFrame
import com.example.blissstories.i9stories.ui.models.StorySetUiState
import com.example.blissstories.models.Story
import com.example.blissstories.utills.hyperbolicTangentInterpolator
import com.example.blissstories.utills.toDpSize
import com.example.blissstories.utills.toPx

@Composable
fun StoriesPlayer(
    modifier: Modifier,
    cornerRadius: Dp,
    fractionOfSize: Float,
    viewModel: StorySetPlayerViewModel,
    close: () -> Unit,
    onHorizontalDrag: (Dp) -> Unit,
    onHorizontalDragEnd: () -> Unit,
    onFinishedStorySet: () -> Unit = {},

    ) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val storySet = viewModel.state

    if (storySet == null) {
        return
    }

    val playerState = remember(storySet.playing) {
        if (storySet.playing) {
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
                viewModel.setPlaying(it)
            },
            onVideoChange = {viewModel.setCurrentStoryIndex(it) }
        )
        exoPlayer.pauseAtEndOfMediaItems = true
        exoPlayer
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
                    viewModel.goToPreviousStory()
                }
            }
            TapType.ShortCenter,
            TapType.ShortRight -> {
                if (storySet!!.isInLastStory()) {
                    onFinishedStorySet()
                } else {
                    viewModel.goToNextStory()
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
                        storySet!!.playing = false
                        totalVerticalDragAmount = 0.dp
                    },
                    onGestureEnd = {
                        storySet!!.playing = true
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
            val progress = remember(storySet, storySet.currentProgress) {
                storySet.currentProgress
            }

            ComposedStoryProgressBar(
                modifier = Modifier
                    .zIndex(2f)
                    .padding(16.dp),
                numberOfStories = storySet.stories.size,
                currentStoryIndex = storySet.currentStoryIndex,
                currentStoryProgress = progress,
            )

            when (storySet.currentStory) {
                is Story.Video -> VideoStoryFrame(
                    modifier = Modifier
                        .size(sizeDp)
                        .zIndex(1f),
                    exoPlayer = exoPlayer,
                    playerState = playerState,
                    currentVideoIndex = storySet.currentVideoIndex,
                    onStoryProgressChange = { viewModel.setProgress(it) },
                    onStoryFinished = onStoryFinished(viewModel, onFinishedStorySet),
                )
                is Story.Static -> StaticStoryPlayer(
                    story = storySet!!.currentStaticStory,
                    playerState = playerState,
                    onStoryProgressChange = { viewModel.setProgress(it)  },
                    onStoryFinished = onStoryFinished(viewModel, onFinishedStorySet),
                    animateFixedItems = storySet.isInFirstStory()
                )
            }
        }
    }
}

private fun onStoryFinished(
    viewModel: StorySetPlayerViewModel,
    onFinishedstorySet: () -> Unit
): () -> Unit = {
    if (viewModel.state.isInLastStory()) {
        onFinishedstorySet()
    } else {
        viewModel.goToNextStory()
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
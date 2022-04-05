package com.example.blissstories.i9stories.frames

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.blissstories.R
import com.example.blissstories.i9stories.StoryFrameState
import com.example.blissstories.models.Story
import com.example.blissstories.rememberTypography
import kotlinx.coroutines.delay
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit

@Composable
fun StaticStoryPlayer(
    modifier: Modifier = Modifier,
    story: Story.Static,
    playerState: StoryFrameState,
    onStoryProgressChange: (Float) -> Unit = {},
    onStoryFinished: () -> Unit = {}
) {
    var storyElapsedTime by remember(story) {
        mutableStateOf(0f)
    }
    var startedPlayingTime by remember(story, playerState) {
        mutableStateOf(LocalDateTime.now())
    }
    val typography = rememberTypography()

    LaunchedEffect(story, playerState, storyElapsedTime) {
        if (storyElapsedTime < story.duration.timeInMs && playerState == StoryFrameState.Playing) {
            val elapsedTime = startedPlayingTime.until(LocalDateTime.now(), ChronoUnit.MILLIS)
            storyElapsedTime = elapsedTime.toFloat()
        }

        if (storyElapsedTime >= story.duration.timeInMs) {
            onStoryFinished()
        }
    }

    var statePhase by remember(story) {
        mutableStateOf(STATE_PHASE_INITIAL)
    }

    val backgroundColor by animateColorAsState(targetValue = story.color)

    Box(
        modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ) {
        Column(Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = statePhase >= STATE_PHASE_PRESENT_IMAGE,
                enter = fadeIn(tween(ANIMATION_TIME_INT)),
                exit = fadeOut()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.image),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxHeight(0.5f)
                        .padding(0.dp)
                )
            }

            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = statePhase >= STATE_PHASE_PRESENT_TITLE,
                    enter = fadeIn(tweenSpec()) + slideInVertically(initialOffsetY = verticalOffset()),
                    exit = fadeOut()
                ) {
                    Text(
                        text = story.title,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(16.dp),
                        style = typography.headingMedium,
                        textAlign = TextAlign.Center
                    )
                }

                AnimatedVisibility(
                    visible = statePhase >= STATE_PHASE_PRESENT_TITLE,
                    enter = fadeIn(tweenSpec()) + slideInVertically(initialOffsetY = verticalOffset()),
                    exit = fadeOut()
                ) {
                    if (story.description != null) {
                        Text(
                            text = story.description,
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(16.dp),
                            style = typography.captionLarge,
                            textAlign = TextAlign.Center
                        )
                    }

                }

                AnimatedVisibility(
                    visible = statePhase >= STATE_PHASE_PRESENT_BUTTON,
                    enter = fadeIn(tweenSpec()) + slideInVertically(initialOffsetY = verticalOffset()),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .padding(16.dp)
                            .height(64.dp)
                            .fillMaxWidth()
                            .border(2.dp, MaterialTheme.colors.secondary)
                        ,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(
                            text = "button",
                            modifier = Modifier.fillMaxSize(),
                            style = typography.captionLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        LaunchedEffect(onStoryProgressChange, storyElapsedTime, block = {
            onStoryProgressChange((storyElapsedTime / story.duration.timeInMs))
        })

        LaunchedEffect(story) {
            statePhase = STATE_PHASE_INITIAL
            delay(ANIMATION_TIME)
            statePhase = STATE_PHASE_PRESENT_IMAGE
            delay(ANIMATION_TIME)
            statePhase = STATE_PHASE_PRESENT_TITLE
            delay(ANIMATION_TIME)
            statePhase = STATE_PHASE_PRESENT_BUTTON
            delay(story.duration.timeInMs - ANIMATION_TIME * 5)
            statePhase = STATE_PHASE_PRESENT_TITLE
            delay(ANIMATION_TIME)
            statePhase = STATE_PHASE_PRESENT_IMAGE
            delay(ANIMATION_TIME)
            statePhase = STATE_PHASE_INITIAL
        }
    }
}

@Composable
private fun verticalOffset(): (fullHeight: Int) -> Int = { it / 2 }

@Composable
private fun tweenSpec(): TweenSpec<Float> =
    tween(ANIMATION_TIME_INT, easing = FastOutLinearInEasing)

@Preview
@Composable
private fun StaticStoryPlayerPreview() {
    StaticStoryPlayer(
        story = Story.Static(Color.Green, Story.Duration.Short, "Graew", order = 1),
        playerState = StoryFrameState.Playing
    )
}

private const val STATE_PHASE_INITIAL = 0
private const val STATE_PHASE_PRESENT_IMAGE = 1
private const val STATE_PHASE_PRESENT_TITLE = 2
private const val STATE_PHASE_PRESENT_BUTTON = 3

private const val ANIMATION_TIME = 400L
private const val ANIMATION_TIME_INT = ANIMATION_TIME.toInt()

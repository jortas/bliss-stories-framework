package com.example.blissstories.i9stories.ui.frames

import androidx.annotation.IntRange
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.blissstories.R
import com.example.blissstories.projectutils.ThemeButtonColors
import com.example.blissstories.i9stories.ui.StoryFrameState
import com.example.blissstories.i9stories.ui.isPlaying
import com.example.blissstories.models.api.StoryDto
import com.example.blissstories.models.domain.Story
import com.example.blissstories.projectutils.rememberTypography
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.min

@Composable
fun StaticStoryPlayer(
    modifier: Modifier = Modifier,
    story: Story.Static,
    playerState: StoryFrameState,
    onStoryProgressChange: (Float) -> Unit = {},
    onStoryFinished: () -> Unit = {},
    animateFixedItems: Boolean = false
) {
    var storyElapsedPlayingTime by remember(story) {
        mutableStateOf(0f)
    }
    var storyElapsedPausedTime by remember(story) {
        mutableStateOf(0f)
    }
    val startedPlayingTime = remember(story, playerState) {
        LocalDateTime.now()
    }

    val typography = rememberTypography()

    LaunchedEffect(story, playerState, storyElapsedPlayingTime) {
        val elapsedTime = startedPlayingTime.until(LocalDateTime.now(), ChronoUnit.MILLIS)
        if (storyElapsedPlayingTime < story.duration.timeInMs) {
            if (playerState.isPlaying()) {
                storyElapsedPlayingTime = elapsedTime.toFloat() - storyElapsedPausedTime
            } else {
                storyElapsedPausedTime = elapsedTime.toFloat() - storyElapsedPlayingTime
            }
        } else {
            onStoryFinished()
        }

    }

    val animatedColor by animateColorAsState(targetValue = story.color)

    val imageAlpha = remember(storyElapsedPlayingTime) {
        if (animateFixedItems) {
            storyAnimate(storyElapsedPlayingTime, story.duration.timeInMs, 0)
        } else {
            1f
        }
    }

    val textAlpha = remember(storyElapsedPlayingTime) {
        storyAnimate(storyElapsedPlayingTime, story.duration.timeInMs, 1, true)
    }

    val textOffset = remember(storyElapsedPlayingTime) {
        32.dp * (1f - storyAnimate(storyElapsedPlayingTime, story.duration.timeInMs, 1))
    }

    val buttonAlpha = remember(storyElapsedPlayingTime) {
        if (animateFixedItems) {
            storyAnimate(storyElapsedPlayingTime, story.duration.timeInMs, 2)
        } else {
            1f
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .background(color = animatedColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            Modifier
                .fillMaxHeight(0.5f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .alpha(imageAlpha)
                    .aspectRatio(1f)
                    .fillMaxHeight()
                    .padding(0.dp)
            )
        }

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = story.title,
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset(y = textOffset)
                    .fillMaxWidth(0.5f)
                    .padding(16.dp),
                style = typography.headingMedium,
                textAlign = TextAlign.Center
            )

            if (story.description != null) {
                Text(
                    text = story.description,
                    modifier = Modifier
                        .alpha(textAlpha)
                        .offset(y = textOffset)
                        .fillMaxWidth(0.5f)
                        .padding(16.dp),
                    style = typography.captionLarge,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {},
                modifier = Modifier
                    .padding(16.dp)
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally)
                    .alpha(buttonAlpha)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp)),
                colors = ThemeButtonColors(story.color),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = "button",
                    style = typography.captionLarge.copy(color = animatedColor),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    LaunchedEffect(onStoryProgressChange, storyElapsedPlayingTime) {
        onStoryProgressChange(min(storyElapsedPlayingTime / story.duration.timeInMs.toFloat(), 1f))
    }

}

private fun storyAnimate(
    storyElapsedTime: Float,
    storyDuration: Long,
    @IntRange(from = 0, to = 5) step: Int,
    reverse: Boolean = false
): Float {
    return when {
        storyElapsedTime < ANIMATION_TIME_INT * step ->
            0f
        storyElapsedTime < ANIMATION_TIME_INT * (step + 1) ->
            FastOutLinearInEasing.transform((storyElapsedTime - ANIMATION_TIME_INT * step) / ANIMATION_TIME_INT)
        reverse && storyElapsedTime > storyDuration - ANIMATION_TIME_INT * (step + 1) ->
            FastOutLinearInEasing.transform((storyDuration - storyElapsedTime - ANIMATION_TIME_INT * step) / ANIMATION_TIME_INT)
        else -> {
            1f
        }
    }
}

@Preview
@Composable
private fun StaticStoryPlayerPreview() {
    StaticStoryPlayer(
        story = Story.Static(Color.Gray, Story.Static.Duration.Short, "Graew", order = 1),
        playerState = StoryFrameState.Playing
    )
}

private const val ANIMATION_TIME = 400L
private const val ANIMATION_TIME_INT = ANIMATION_TIME.toInt()
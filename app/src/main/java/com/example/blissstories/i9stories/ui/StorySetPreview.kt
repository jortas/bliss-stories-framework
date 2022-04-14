package com.example.blissstories.i9stories.ui

import androidx.annotation.IntRange
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.blissstories.R
import com.example.blissstories.models.StoryPreview
import com.example.blissstories.models.mocks.storyPreviewMock
import com.example.blissstories.projectutils.rememberTypography
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit

@Composable
fun StorySetPreview(
    modifier: Modifier = Modifier,
    storyPreview: StoryPreview,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    var storyElapsedPlayingTime by remember(storyPreview) {
        mutableStateOf(0f)
    }
    val startedPlayingTime = remember(storyPreview) {
        LocalDateTime.now()
    }

    val typography = rememberTypography()

    LaunchedEffect(storyPreview, storyElapsedPlayingTime) {
        val elapsedTime = startedPlayingTime.until(LocalDateTime.now(), ChronoUnit.MILLIS)
        storyElapsedPlayingTime = elapsedTime.toFloat()
    }

    val animatedBackgroundColor by animateColorAsState(
        targetValue = storyPreview.backgroundColor(
            context
        )
    )

    val imageAlpha = remember(storyElapsedPlayingTime) {
        storyAnimate(storyElapsedPlayingTime, 0)
    }

    val textAlpha = remember(storyElapsedPlayingTime) {
        storyAnimate(storyElapsedPlayingTime, 1)
    }

    val textOffset = remember(storyElapsedPlayingTime) {
        8.dp * (1f - storyAnimate(storyElapsedPlayingTime, 1))
    }


    Box(
        modifier
            .background(color = animatedBackgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 36.dp)
                    .size(64.dp)
                    .alpha(imageAlpha)
            )

            Text(
                text = storyPreview.title,
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset(y = textOffset)
                    //   .fillMaxWidth(0.5f)
                    .padding(8.dp),
                style = typography.caption,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun storyAnimate(
    storyElapsedTime: Float,
    @IntRange(from = 0, to = 5) step: Int
): Float {
    return when {
        storyElapsedTime < ANIMATION_TIME_INT * step ->
            0f
        storyElapsedTime < ANIMATION_TIME_INT * (step + 1) ->
            FastOutLinearInEasing.transform((storyElapsedTime - ANIMATION_TIME_INT * step) / ANIMATION_TIME_INT)
        else -> {
            1f
        }
    }
}

@Preview
@Composable
private fun StorySetPreviewPreview() {
    StorySetPreview(
        modifier = Modifier.size(136.dp, 160.dp),
        storyPreview = storyPreviewMock(),
        onClick = {}
    )
}

private const val ANIMATION_TIME = 400L
private const val ANIMATION_TIME_INT = ANIMATION_TIME.toInt()
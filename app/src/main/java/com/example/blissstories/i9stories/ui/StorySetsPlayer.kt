package com.example.blissstories.i9stories.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.blissstories.utills.animateDpSizeAsState
import com.example.blissstories.utills.hyperbolicTangentInterpolator
import com.example.blissstories.utills.middleMinInterpolation
import kotlinx.coroutines.delay
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

@Composable
fun StorySetsPlayer(
    modifier: Modifier = Modifier,
    viewModel: StorySetsPlayerViewModel,
    initialRadius: Dp = 4.dp,
    initialSize: DpSize = DpSize(1.dp, 1.dp),
    initialPosition: Offset = Offset(0f, 0f),
    close: () -> Unit,
    onFinishedStorySets: () -> Unit = {},
) {

    val focusedStoryIndex = viewModel.currentStorySetIndex
    var justLaunched by remember { mutableStateOf(true) }
    var closeEvent by remember { mutableStateOf(false) }

    LaunchedEffect("init") {
        justLaunched = false
    }

    val entryAnimation = remember(
        justLaunched,
        closeEvent
    ) { justLaunched || closeEvent }

    val alphaBackground by animateFloatAsState(
        targetValue = if (entryAnimation) 0f else 1f,
        animationSpec = storiesSetAnimationSpec(0.8f, entryAnimation)
    )

    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(Color.Black.copy(alphaBackground))
            .offset(x = 0.dp),
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(closeEvent) {
            if (closeEvent) {
                delay(ANIMATION_ENTRY_DURATION)
                close()
                closeEvent = false
            }
        }

        val radius by animateDpAsState(
            targetValue = if (entryAnimation) initialRadius else 0.dp,
            animationSpec = storiesSetAnimationSpec()
        )

        val shape = remember(radius) { RoundedCornerShape(radius) }
        val alpha by animateFloatAsState(
            targetValue = if (entryAnimation) 0f else 1f,
            animationSpec = storiesSetAnimationSpec(0.2f, entryAnimation)
        )

        val maxSizeDp = remember(maxWidth, maxHeight) {
            DpSize(
                maxWidth,
                maxHeight
            )
        }
        val horizontalDragAmount = remember(viewModel) { mutableStateOf(-maxSizeDp.width * focusedStoryIndex) }

        val snapValue: MutableState<Dp?> = remember { mutableStateOf(null) }
        val savedHorizontalDragAmount =
            remember(viewModel) { mutableStateOf(horizontalDragAmount.value) }
        val animatingHorizontalDrag = remember { mutableStateOf(false) }

        SnapOnDrag(
            snapValue,
            horizontalDragAmount,
            animatingHorizontalDrag,
            savedHorizontalDragAmount
        )

        val limitDragOnNull = remember(maxSizeDp) {
            maxSizeDp.width.value - maxSizeDp.width.value / GOLD_RATIO
        }

        val initialOffset = remember(initialPosition) {
            Offset(
                initialPosition.x - maxWidth.value / 2,
                initialPosition.y - maxHeight.value / 2
            )
        }

        val size by animateDpSizeAsState(
            targetValue = if (entryAnimation) initialSize else maxSizeDp,
            animationSpec = storiesSetAnimationSpec()
        )

        val offset by animateOffsetAsState(
            targetValue = if (entryAnimation) initialOffset else Offset(0f, 0f),
            animationSpec = storiesSetAnimationSpec()
        )

        Box(
            Modifier
                .offset(offset.x.dp, offset.y.dp)
                .scale(size.width.value / maxWidth.value, size.height.value / maxHeight.value)
                .clip(shape),
            contentAlignment = Alignment.Center
        ) {
            val roundBobbinSize = remember { STORIES_ROUND_BOBBIN_SIZE }
            for (i in -(roundBobbinSize - 1) / 2..roundBobbinSize / 2) {
                val storySetIndex = remember(focusedStoryIndex) {
                    indexOfStoriesRoundBobbin(focusedStoryIndex, roundBobbinSize, i)
                }
                if (storySetIndex >= 0 && storySetIndex <= viewModel.viewModels.lastIndex) {
                    val horizontalOffset = remember(horizontalDragAmount.value) {
                        (maxSizeDp.width) * (storySetIndex) + horizontalDragAmount.value
                    }

                    //from 0.9f to 1f
                    val fractionOfSize = remember(horizontalDragAmount.value) {
                        scaleOnHorizontalDrag(horizontalDragAmount.value, maxSizeDp.width)
                    }

                    val radiusSize = remember(horizontalDragAmount.value) {
                        radiusOnHorizontalDrag(horizontalDragAmount.value, maxSizeDp.width)
                    }

                    StoriesPlayer(
                        cornerRadius = radiusSize,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alpha)
                            .offset(x = horizontalOffset),
                        viewModel = viewModel.currentStoryViewModel,
                        fractionOfSize = fractionOfSize,
                        close = { closeEvent = true },
                        onFinishedStorySet = { viewModel.goToNextStorySet() },
                        onHorizontalDrag = { drag ->
                            onHorizontalDrag(
                                focusedStoryIndex,
                                viewModel.viewModels.lastIndex,
                                limitDragOnNull,
                                horizontalDragAmount,
                                savedHorizontalDragAmount.value
                            )(drag)
                        },
                        onHorizontalDragEnd = {
                            val direction =
                                (savedHorizontalDragAmount.value - horizontalDragAmount.value).value.sign
                            savedHorizontalDragAmount.value = horizontalDragAmount.value
                            snapValue.value =
                                maxSizeDp.width * (horizontalDragAmount.value / maxSizeDp.width.value).value.roundToInt()
                            if (snapValue.value!! < horizontalDragAmount.value && direction == 1f) {
                                viewModel.goToNextStorySet()
                            } else if (snapValue.value!! > horizontalDragAmount.value && direction == -1f) {
                                viewModel.goToPreviousStorySet()
                            }
                        })
                }
            }
        }
    }
}

@Composable
private fun <T> storiesSetAnimationSpec(
    factor: Float = 1f,
    reversing: Boolean = false
): TweenSpec<T> {
    val delay = if (reversing) (ANIMATION_ENTRY_DURATION * (1f - factor)).toInt() else 0
    return TweenSpec(
        (ANIMATION_ENTRY_DURATION * factor).toInt(),
        easing = FastOutLinearInEasing,
        delay = delay
    )
}

@Composable
private fun SnapOnDrag(
    snapValue: MutableState<Dp?>,
    horizontalDragAmount: MutableState<Dp>,
    animatingHorizontalDrag: MutableState<Boolean>,
    savedHorizontalDragAmount: MutableState<Dp>
) {
    var endAnimationTime by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(snapValue.value, horizontalDragAmount.value, animatingHorizontalDrag.value) {
        val now = LocalDateTime.now()
        if (snapValue.value == null) {
            return@LaunchedEffect
        } else if (!animatingHorizontalDrag.value) {
            endAnimationTime =
                LocalDateTime.now().plus(ANIMATION_TIME_HORIZONTAL_SNAP, ChronoUnit.MILLIS)
            animatingHorizontalDrag.value = true
        }

        val progress = now.until(endAnimationTime, ChronoUnit.MILLIS)
            .toFloat() / ANIMATION_TIME_HORIZONTAL_SNAP
        horizontalDragAmount.value =
            savedHorizontalDragAmount.value +
                    (snapValue.value!! - savedHorizontalDragAmount.value) * (1 - FastOutLinearInEasing.transform(
                progress
            ))
        if (now.isAfter(endAnimationTime)) {
            animatingHorizontalDrag.value = false
            horizontalDragAmount.value = snapValue.value!!
            snapValue.value = null
            savedHorizontalDragAmount.value = horizontalDragAmount.value
        }
    }
}

private fun radiusOnHorizontalDrag(
    horizontalDragAmount: Dp,
    maxWidth: Dp
) = MAX_RADIUS_IN_HORIZONTAL_TRANSITION.dp * middleMinInterpolation(
    horizontalDragAmount,
    maxWidth,
    MAX_RADIUS_FRACTION_IN_HORIZONTAL_TRANSITION,
    MIN_RADIUS_FRACTION_IN_HORIZONTAL_TRANSITION,
)

private fun scaleOnHorizontalDrag(
    horizontalDragAmount: Dp,
    maxWidth: Dp
) = middleMinInterpolation(
    horizontalDragAmount,
    maxWidth,
    MIN_SIZE_FRACTION_IN_HORIZONTAL_TRANSITION,
    MAX_SIZE_FRACTION_IN_HORIZONTAL_TRANSITION,
)

private fun onHorizontalDrag(
    storySetIndex: Int,
    storySetLastIndex: Int,
    limitDragOnNull: Float,
    horizontalDragAmount: MutableState<Dp>,
    savedHorizontalDragAmount: Dp
): (Dp) -> Unit {
    return { dragAmount: Dp ->
        val newDragAmount =
            if (storySetIndex == 0 && dragAmount > 0.dp || storySetIndex == storySetLastIndex && dragAmount < 0.dp) {
                hyperbolicTangentInterpolator(
                    abs(dragAmount.value), 0f, limitDragOnNull
                ).dp * dragAmount.value.sign
            } else {
                dragAmount
            }
        horizontalDragAmount.value = savedHorizontalDragAmount + newDragAmount
    }
}

//This is for roundBobbin story player, players should have the following index being 0 the first
//in the screen, and the rest are on the left and right off the screen
// -1 0 1
//0 should be the first player that appears in the center
//This function will also work with 4, 5 or more players, in these two formats
//-1 0 1 2 or -2 -1 0 1 2
fun indexOfStoriesRoundBobbin(
    currentFocusedIndex: Int,
    roundBobbinSize: Int,
    playerIndex: Int
): Int {
    return (((currentFocusedIndex - playerIndex + roundBobbinSize / 2) / roundBobbinSize)) * roundBobbinSize + playerIndex
}

fun abs(dp: Dp): Dp {
    return abs(dp.value).dp
}

internal operator fun Dp.rem(other: Dp): Dp {
    return (this.value % other.value).dp
}

internal const val MAX_SIZE_FRACTION_IN_HORIZONTAL_TRANSITION = 1f
internal const val MIN_SIZE_FRACTION_IN_HORIZONTAL_TRANSITION = 0.9f
internal const val MAX_RADIUS_IN_HORIZONTAL_TRANSITION = 24
internal const val MAX_RADIUS_FRACTION_IN_HORIZONTAL_TRANSITION = 1f
internal const val MIN_RADIUS_FRACTION_IN_HORIZONTAL_TRANSITION = 0f

private const val ANIMATION_TIME_HORIZONTAL_SNAP = 400L
private const val ANIMATION_ENTRY_DURATION = 200L

internal const val STORIES_ROUND_BOBBIN_SIZE = 3
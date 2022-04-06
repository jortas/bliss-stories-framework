package com.example.blissstories.i9stories

import android.util.Log
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.blissstories.models.StorySet
import com.example.blissstories.utills.animateDpSizeAsState
import com.example.blissstories.utills.hyperbolicTangentInterpolator
import com.example.blissstories.utills.middleMinInterpolation
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

@Composable
fun StoriesSetPlayer(
    modifier: Modifier = Modifier,
    initialShape: Shape = RoundedCornerShape(4.dp),
    initialSize: DpSize = DpSize(1.dp, 1.dp),
    animateEntry: Boolean = true,
    storySetsList: List<StorySet>,
    close: () -> Unit,
    onFinishedStorySets: () -> Unit = {},
) {
    val shape = RoundedCornerShape(4.dp)
    val horizontalDragAmount = remember { mutableStateOf(0.dp) }
    val snapValue: MutableState<Dp?> = remember { mutableStateOf(null) }
    val savedHorizontalDragAmount = remember { mutableStateOf(0.dp) }
    val animatingHorizontalDrag = remember { mutableStateOf(false) }

    SnapOnDrag(snapValue, horizontalDragAmount, animatingHorizontalDrag, savedHorizontalDragAmount)

    var focusedIndex by remember { mutableStateOf(0) }
    var justLaunched by remember { mutableStateOf(true) }
    var closeEvent by remember { mutableStateOf(false) }

    LaunchedEffect("init") {
        justLaunched = false
    }

    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(0.dp))
            .background(Color.Black)
            .offset(x = 0.dp),
        contentAlignment = Alignment.Center
    ) {
        val maxSizeDp = remember(maxWidth, maxHeight) {
            DpSize(
                maxWidth,
                maxHeight
            )
        }

        val limitDragOnNull = remember(maxSizeDp) {
            maxSizeDp.width.value - maxSizeDp.width.value / GOLD_RATIO
        }

        val size by animateDpSizeAsState(
            targetValue = if (justLaunched && animateEntry || closeEvent) initialSize else {
                maxSizeDp
            }, finishedListener = { size ->
                if (closeEvent) {
                    close()
                    closeEvent = false
                }
            })

        Box(
            Modifier
                .scale(size.width.value / maxWidth.value, size.height.value / maxHeight.value),
            contentAlignment = Alignment.Center
        ) {
            val roundBobbinSize = remember { 4 }
            for (i in -(roundBobbinSize - 1) / 2..roundBobbinSize / 2) {
                val storySetIndex = remember(focusedIndex) {
                    indexOfStoriesRoundBobbin(focusedIndex, roundBobbinSize, i)
                }
                if (storySetIndex >= 0 && storySetIndex <= storySetsList.lastIndex) {
                    val horizontalOffset =
                        remember(horizontalDragAmount) { (maxSizeDp.width) * (storySetIndex) + horizontalDragAmount.value }

                    //from 0.9f to 1f
                    val fractionOfSize = remember(horizontalDragAmount) {
                        scaleOnHorizontalDrag(horizontalDragAmount.value, maxSizeDp.width)
                    }

                    val radiusSize = remember(horizontalDragAmount) {
                        radiusOnHorizontalDrag(horizontalDragAmount.value, maxSizeDp.width)
                    }

                    StoriesPlayer(cornerRadius = radiusSize,
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = horizontalOffset),
                        storySet = storySetsList[storySetIndex],
                        fractionOfSize = fractionOfSize,
                        close = { closeEvent = true },
                        onFinishedStorySet = { focusedIndex += 1 },
                        onHorizontalDrag =
                            onHorizontalDrag(
                                storySetIndex,
                                storySetsList.lastIndex,
                                limitDragOnNull,
                                horizontalDragAmount,
                                savedHorizontalDragAmount.value
                            )
                        ,
                        onHorizontalDragEnd = {
                            savedHorizontalDragAmount.value = horizontalDragAmount.value
                            snapValue.value =
                                maxSizeDp.width * (horizontalDragAmount.value / maxSizeDp.width.value).value.roundToInt()
                        })
                }
            }
        }
    }
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

@Composable
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
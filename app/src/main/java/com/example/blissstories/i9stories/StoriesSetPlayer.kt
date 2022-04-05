package com.example.blissstories.i9stories

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.blissstories.models.StorySet
import com.example.blissstories.utills.animateDpSIzeAsState
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.roundToInt

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
    var horizontalDragAmount by remember { mutableStateOf(0.dp) }
    var snapValue by remember { mutableStateOf(0.dp) }

    LaunchedEffect(snapValue) {
        if (snapValue==0.dp){
            return@LaunchedEffect
        }
        var now = LocalDateTime.now()
        val end = now.plus(500L, ChronoUnit.MILLIS)
        val initialDrag = horizontalDragAmount.value
        while (now.isBefore(end)) {
            now = LocalDateTime.now()
            val progress = now.until(end, ChronoUnit.MILLIS) / 500L
            horizontalDragAmount =
                initialDrag.dp + (snapValue - initialDrag.dp) * FastOutLinearInEasing.transform(
                    progress.toFloat()
                )
        }
    }

    var savedHorizontalDragAmount by remember { mutableStateOf(0.dp) }
    var focusedIndex by remember { mutableStateOf(0) }

    var justLaunched by remember { mutableStateOf(true) }
    var closeEvent by remember { mutableStateOf(false) }

    LaunchedEffect("init") {
        justLaunched = false
    }

    BoxWithConstraints(
        modifier
            .fillMaxSize()
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

        val size by animateDpSIzeAsState(
            targetValue = if (justLaunched && animateEntry || closeEvent) initialSize else {
                maxSizeDp
            }, finishedListener = { size ->
                if (closeEvent) {
                    close()
                    closeEvent = false
                }
            })

        @Composable
        fun StoriesPlayerForSet(index: Int, roundBobbinSize: Int) {
            val storyIndex = remember(focusedIndex) {
                indexOfStoriesRoundBobbin(focusedIndex, roundBobbinSize, index)
            }
            if (storyIndex < 0 || storyIndex > storySetsList.lastIndex) {
                return
            }
            val offset = (maxSizeDp.width) * (storyIndex) + horizontalDragAmount
            //from 0.9f to 1f
            val fractionOfSize by remember(horizontalDragAmount) {
                mutableStateOf(
                    middleMinInterpolation(
                        horizontalDragAmount,
                        maxSizeDp.width,
                        MIN_SIZE_FRACTION_IN_HORIZONTAL_TRANSITION,
                        MAX_SIZE_FRACTION_IN_HORIZONTAL_TRANSITION,
                    )
                )
            }

            val radiusSize = remember(horizontalDragAmount) {
                MAX_RADIUS_IN_HORIZONTAL_TRANSITION.dp * middleMinInterpolation(
                    horizontalDragAmount,
                    maxSizeDp.width,
                    MAX_RADIUS_FRACTION_IN_HORIZONTAL_TRANSITION,
                    MIN_RADIUS_FRACTION_IN_HORIZONTAL_TRANSITION,
                )
            }


            StoriesPlayer(
                cornerRadius = radiusSize,
                modifier = Modifier
                    .fillMaxSize(fractionOfSize)
                    .offset(x = offset),
                storySet = storySetsList[storyIndex],
                close = { closeEvent = true },
                onFinishedStorySet = { focusedIndex += 1 },
                onHorizontalDrag = { horizontalDragAmount = savedHorizontalDragAmount + it },
                onHorizontalDragEnd = {
                    snapValue =
                        maxWidth * (horizontalDragAmount.value / maxWidth.value).roundToInt()
                    savedHorizontalDragAmount = snapValue
                }
            )
        }
        Box(
            Modifier
                .size(size),
            contentAlignment = Alignment.Center
        ) {
            val roundBobbinSize = remember { 4 }
            for (i in -(roundBobbinSize - 1) / 2..roundBobbinSize / 2) {
                StoriesPlayerForSet(i, roundBobbinSize)
            }
        }
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

//Cyclic interpolation of a value starting in max going to min in the middle of the cycle
// and coming back to max in the end of the cycle
// MIN + | x % w - w/2| / (w/2) (MAX - MIN)
fun middleMinInterpolation(
    x: Dp,
    intervalCycle: Dp,
    minValue: Float,
    maxValue: Float
): Float {
    return minValue + (abs(abs(x) % intervalCycle - intervalCycle / 2) / (intervalCycle / 2) * (maxValue - minValue))
}

fun abs(dp: Dp): Dp {
    return abs(dp.value).dp
}

private operator fun Dp.rem(other: Dp): Dp {
    return (this.value % other.value).dp
}

private const val MAX_SIZE_FRACTION_IN_HORIZONTAL_TRANSITION = 1f
private const val MIN_SIZE_FRACTION_IN_HORIZONTAL_TRANSITION = 0.9f
private const val MAX_RADIUS_IN_HORIZONTAL_TRANSITION = 24
private const val MAX_RADIUS_FRACTION_IN_HORIZONTAL_TRANSITION = 1f
private const val MIN_RADIUS_FRACTION_IN_HORIZONTAL_TRANSITION = 0f
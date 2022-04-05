package com.example.blissstories.i9stories

import android.util.Log
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.*
import com.example.blissstories.models.StorySet
import com.example.blissstories.utills.animateDpSIzeAsState

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
    var horizontalDragAmount by remember {
        mutableStateOf(0.dp)
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
            StoriesPlayer(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = offset),
                storySet = storySetsList[storyIndex],
                close = { closeEvent = true },
                onFinishedStorySet = { focusedIndex += 1 },
                onHorizontalDrag = { horizontalDragAmount = savedHorizontalDragAmount + it },
                onHorizontalDragEnd = {
                    savedHorizontalDragAmount = horizontalDragAmount
                    focusedIndex += 1
                }
            )
        }

        Box(Modifier.size(size)) {
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
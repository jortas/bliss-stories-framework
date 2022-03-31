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

@Composable
fun StoriesSetPlayer(
    modifier: Modifier = Modifier,
    initialShape: Shape = RoundedCornerShape(4.dp),
    initialSize: Size = Size(1f, 1f),
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

    var justLaunched by remember { mutableStateOf(true) }
    var closeEvent by remember { mutableStateOf(false) }

    LaunchedEffect("init") {
        justLaunched = false
    }

    BoxWithConstraints(
        modifier
            .size(300.dp)
            .offset(x = 0.dp),
        contentAlignment = Alignment.Center
    ) {
        val maxSize = remember(maxWidth, maxHeight) {
            Size(
                maxWidth.value,
                maxHeight.value
            )
        }
        val maxSizeDp = remember(maxSize) {
            DpSize(maxSize.width.dp, maxSize.height.dp)
        }

        val size by animateSizeAsState(
            targetValue = if (justLaunched && animateEntry || closeEvent) initialSize else {
                maxSize
            }, finishedListener = { size ->
                if (closeEvent) {
                    close()
                    closeEvent = false
                }
            })


        Box() {
            StoriesPlayer(
                size = size,
                modifier = Modifier
                    .size(maxSizeDp)
                    .offset(x = -maxSizeDp.width + horizontalDragAmount),
                storySet = null,
                close = { closeEvent = true },
                onFinishedStorySet = {},
                onHorizontalDrag = { horizontalDragAmount = savedHorizontalDragAmount + it },
                onHorizontalDragEnd = { savedHorizontalDragAmount = horizontalDragAmount }
            )
            StoriesPlayer(
                size = size,
                modifier = Modifier
                    .size(maxSizeDp)
                    .offset(x = horizontalDragAmount),
                storySet = storySetsList[0],
                close = { closeEvent = true },
                onFinishedStorySet = {},
                onHorizontalDrag = { horizontalDragAmount = savedHorizontalDragAmount + it },
                onHorizontalDragEnd = { savedHorizontalDragAmount = horizontalDragAmount }
            )
            StoriesPlayer(
                size = size,
                modifier = Modifier
                    .size(maxSizeDp)
                    .offset(x = maxSizeDp.width + horizontalDragAmount),
                storySet = storySetsList[1],
                close = { closeEvent = true },
                onFinishedStorySet = {},
                onHorizontalDrag = { horizontalDragAmount = savedHorizontalDragAmount + it },
                onHorizontalDragEnd = { savedHorizontalDragAmount = horizontalDragAmount }
            )
        }
    }
}
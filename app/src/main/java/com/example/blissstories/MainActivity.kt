package com.example.blissstories

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.blissstories.i9stories.StoriesSetPlayer
import com.example.blissstories.i9stories.StorySetPreview
import com.example.blissstories.models.mocks.staticStoryFactoryMock
import com.example.blissstories.models.mocks.storyFactoryMock

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val composeView = this.findViewById<ComposeView>(R.id.composeView)

        val stories =
            listOf(
                staticStoryFactoryMock(),
                storyFactoryMock(),
                staticStoryFactoryMock(),
                storyFactoryMock(),
                storyFactoryMock(),
                staticStoryFactoryMock(),
                staticStoryFactoryMock(),
                //   staticStoryFactoryMock(),
                //   staticStoryFactoryMock()
            )

        composeView.apply {
            // Dispose the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val density = LocalDensity.current

                // In Compose world
                MaterialTheme {
                    var storyToOpen: Int? by remember {
                        mutableStateOf(null)
                    }

                    val cornerRadius = 4.dp
                    val size = DpSize(136.dp, 160.dp)
                    var centerOfClickedItem by remember {
                        mutableStateOf(Offset(0f, 0f))
                    }

                    val lazyRowState = rememberLazyListState()

                    @Composable
                    fun clickOnStoryPreview(index: Int): () -> Unit {
                        return {
                            with(density) {
                                centerOfClickedItem = Offset(
                                    lazyRowState.layoutInfo.visibleItemsInfo[index - lazyRowState.firstVisibleItemIndex].offset.toDp().value -
                                            lazyRowState.layoutInfo.viewportStartOffset.toDp().value +
                                            size.width.value / 2,
                                    (size.height.value / 2f) + 16f,
                                )
                                storyToOpen = index
                            }
                        }
                    }

                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {

                        LazyRow(
                            Modifier.fillMaxWidth(),
                            state = lazyRowState,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            itemsIndexed(stories) { index, storySet ->
                                StorySetPreview(
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .size(size)
                                        .clip(RoundedCornerShape(cornerRadius)),
                                    storyPreview = storySet.preview,
                                    onClick = clickOnStoryPreview(index)
                                )
                            }
                        }
                    }

                    if (storyToOpen != null) {
                        StoriesSetPlayer(
                            initialStorySetIndex = storyToOpen!!,
                            initialRadius = cornerRadius,
                            initialSize = size,
                            initialPosition = centerOfClickedItem,
                            storySetsList = stories,
                            close = { storyToOpen = null },
                            onFinishedStorySets = {}
                        )
                    }
                }
            }
        }
    }
}
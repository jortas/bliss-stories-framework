package com.example.blissstories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.example.blissstories.i9stories.StoryFramework
import com.example.blissstories.models.storyFactoryMock
import com.example.blissstories.utills.ButtonForStory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val composeView = this.findViewById<ComposeView>(R.id.composeView)

        composeView.apply {
            // Dispose the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val widthButton = 30.dp
                val heightButton = 60.dp
                // In Compose world
                MaterialTheme {
                    var open by remember {
                        mutableStateOf(false)
                    }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val shape = RoundedCornerShape(4.dp)
                        ButtonForStory(
                            shape = shape,
                            modifier = Modifier.size(widthButton, heightButton),
                            onClick = { open = true }
                        )
                        if (open) {
                            StoryFramework(
                                initialShape = shape,
                                initialSize = Size(widthButton.value, heightButton.value),
                                modifier = Modifier.fillMaxSize(),
                                storySet = storyFactoryMock(),
                                close = {open = false},
                                finishedStorySetAction = {}
                            )
                        }
                    }
                }
            }
        }
    }
}
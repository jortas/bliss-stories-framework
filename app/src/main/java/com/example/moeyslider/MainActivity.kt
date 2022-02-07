package com.example.moeyslider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.moeyslider.i9stories.StoryFramework
import com.example.moeyslider.models.storyFactoryMock

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
                // In Compose world
                MaterialTheme {
                    Box(Modifier.fillMaxSize()) {
                        StoryFramework(
                            modifier = Modifier.fillMaxSize(),
                            storySet = storyFactoryMock(),
                            {},
                            {}
                        )
                    }
                }
            }
        }
    }
}
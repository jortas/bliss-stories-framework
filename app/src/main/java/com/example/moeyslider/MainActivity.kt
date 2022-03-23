package com.example.moeyslider

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.example.moeyslider.i9stories.StoryFramework
import com.example.moeyslider.models.storyFactoryMock

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val composeView: ComposeView = this.findViewById(R.id.composeView)

        composeView.apply {
            // Dispose the Composxition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                MaterialTheme {
                    with(LocalDensity.current) {
//                        Box(Modifier.size(getScreenWidth().toDp(), getScreenHeight().toDp())) {
                        Box(
                            Modifier
                                .fillMaxSize()
                        ) {
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
}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}
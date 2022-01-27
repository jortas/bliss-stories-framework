package com.example.moeyslider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val composeView = this.findViewById<ComposeView>(R.id.composeView)
        val liveData  = MutableLiveData(0f)
        val blueColor = Color(0xFF71B9E3)

        composeView.apply {
            // Dispose the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                MaterialTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)

                    ) {

                        val sliderValue = liveData.observeAsState(0f)

                        val interactionSource = remember {
                            MutableInteractionSource()
                        }

                        var enabled by remember { mutableStateOf(false) }

                        val wasPressed = interactionSource.collectIsPressedAsState()

                        if (!enabled && wasPressed.value){
                            enabled = true
                        }

                    }
                }
            }
        }
    }
}
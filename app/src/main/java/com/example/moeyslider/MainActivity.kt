package com.example.moeyslider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.example.moeyslider.slider.BlissSliderColors
import com.example.moeyslider.slider.Slider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val composeView = this.findViewById<ComposeView>(R.id.composeView)
        val blueColor =  Color(0xFF71B9E3)

        composeView.apply {
            // Dispose the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                MaterialTheme {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {

                        var sliderPosition by remember { mutableStateOf(0f) }

                        Text(text = sliderPosition.toString())

                        Row(
                            Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        Pair(0f, blueColor), Pair(01f, Color(0xFFAEB8BA))
                                    )
                                )
                                .fillMaxWidth()
                                .height(30.dp)
                        ) {

                        }

                        Slider(
                            value = sliderPosition,
                            onValueChange = { sliderPosition = it },
                            values = listOf(-0.5f, 0.1f, 0.4f, 0.6f, 1f, 2f),
                            trackColors = BlissSliderColors.Defaults.track(
                                activeBrush = Brush.horizontalGradient(
                                    listOf(
                                        blueColor, Color(0xFFAEB8BA)
                                    ),
                                    tileMode = TileMode.Clamp
                                ),
                                inactiveColor = Color(0x2271B9E3),
                            ),
                            tickColors = BlissSliderColors.Defaults.tick(
                                activeColor = Color.White,
                                inactiveColor = blueColor.copy(alpha=0.3f)
                            ),
                            thumbColors = BlissSliderColors.Defaults.thumb(
                                color = blueColor
                            ),
                        )

                        Slider(
                            value = sliderPosition,
                            onValueChange = { sliderPosition = it },
                            constructorValueRange = 0f..1f,
                            trackColors = BlissSliderColors.Defaults.track(
                                activeBrush = Brush.horizontalGradient(
                                    listOf(
                                        blueColor, Color(0xFFAEB8BA)
                                    ),
                                ),
                                inactiveColor = Color(0x2271B9E3)
                            ),
                            tickColors = BlissSliderColors.Defaults.tick(
                                activeColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

//                 val brushCircle = Brush.horizontalGradient(
//                                Pair(0f, blueColor), Pair(sliderPosition, Color(0xFF979797))
//                            )
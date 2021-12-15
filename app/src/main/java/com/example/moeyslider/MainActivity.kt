package com.example.moeyslider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.example.moeyslider.slider.BlissSliderColors
import com.example.moeyslider.slider.BlissSlider
import com.example.moeyslider.slider.BlissSliderParams

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val composeView = this.findViewById<ComposeView>(R.id.composeView)
        val slider: Slider = this.findViewById(R.id.slider)
        val liveData  = MutableLiveData(2f)
        slider.value = liveData
        val blueColor = Color(0xFF71B9E3)

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

                        val sliderValue = liveData.observeAsState(0f)

                        Text(text = sliderValue.value.toString())

                        BlissSlider(
                            BlissSliderParams(
                            value = sliderValue.value,
                            onValueChange = { liveData.value = it },
                            constructorValueRange = 0f..2f,
                            values = listOf(0.1f, 0.4f, 0.6f, 1f, 2f),
                            trackColors = BlissSliderColors.Track(
                                activeBrush = Brush.horizontalGradient(
                                    listOf(
                                        blueColor, Color(0xFFAEB8BA)
                                    ),
                                    tileMode = TileMode.Clamp
                                ),
                                inactiveColor = Color(0x2271B9E3),
                            ),
                            tickColors = BlissSliderColors.Tick(
                                activeColor = Color.White,
                                inactiveColor = blueColor.copy(alpha = 0.3f)
                            ),
                            thumbColors = BlissSliderColors.Thumb(
                                color = blueColor
                            ),
                        )
                        )
                    }
                }
            }
        }
    }
}
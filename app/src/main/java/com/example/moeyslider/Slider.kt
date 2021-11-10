package com.example.moeyslider

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moeyslider.slider.BlissSliderColors
import com.example.moeyslider.slider.Slider


@SuppressLint("ClickableViewAccessibility")
class Slider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {
    val blueColor =  Color(0xFF71B9E3)

    var value: MutableLiveData<Float> = MutableLiveData(0f)


    @Composable
    override fun Content() {

        var sliderValue = value.observeAsState(0f)

        MaterialTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {


                Slider(
                    value = sliderValue.value,
                    onValueChange = {value.value = it  },
                    constructorValueRange = 0f..2f,
                    values = listOf(0.1f, 0.4f, 0.6f, 1f, 2f),
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
                        inactiveColor = blueColor.copy(alpha = 0.3f)
                    ),
                    thumbColors = BlissSliderColors.Defaults.thumb(
                        color = blueColor
                    ),
                )
            }
        }
    }
}
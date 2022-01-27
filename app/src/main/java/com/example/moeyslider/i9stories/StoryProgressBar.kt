package com.example.moeyslider.i9stories

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.i9.app.ui.seek.i9slider.BlissSlider
import pt.i9.app.ui.seek.i9slider.BlissSliderColors

@Composable
fun StoryProgressBar(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 1.0) progress: Float
) {

    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .padding(2.dp)
            .background(
                Color.Gray, shape
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(
                    Color.White, shape
                )
        )
    }
}


@Preview
@Composable
private fun BlissSliderPreview() {
    MaterialTheme {
        Box(
            Modifier
                .fillMaxWidth()
                .height(16.dp)
        ) {
            StoryProgressBar(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f),
                progress = 1f
            )
        }
    }
}
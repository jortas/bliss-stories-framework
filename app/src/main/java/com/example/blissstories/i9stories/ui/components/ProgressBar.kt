package com.example.blissstories.i9stories.ui.components

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProgressStep(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 1.0) progress: Float
) {

    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
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
            ProgressStep(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f),
                progress = 1f
            )
        }
    }
}
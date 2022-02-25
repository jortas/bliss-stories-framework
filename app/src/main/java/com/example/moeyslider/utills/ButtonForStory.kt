package com.example.moeyslider.utills

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moeyslider.i9stories.ComposedStoryProgressBar

@Composable
fun ButtonForStory(
    shape: Shape,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
    ) {
    Button(
        modifier = modifier.background(Color.Blue, shape),
        onClick = onClick
    ) {
    }
}


@Preview
@Composable
fun ButtonForStoryPreview(){
    MaterialTheme {
        ButtonForStory(
            shape = RectangleShape
        )
    }
}
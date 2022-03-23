package com.example.blissstories.utills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ButtonForStory(
    shape: Shape,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier
            .padding(0.dp)
            .background(Color.Transparent, shape)
            .fillMaxSize(),
        onClick = onClick
    ) {
    }
}


@Preview
@Composable
fun ButtonForStoryPreview() {
    MaterialTheme {
        ButtonForStory(
            shape = RectangleShape
        )
    }
}
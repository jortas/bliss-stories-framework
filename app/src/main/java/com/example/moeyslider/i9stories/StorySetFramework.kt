package com.example.moeyslider.i9stories

import androidx.compose.foundation.gestures.GestureCancellationException
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.moeyslider.models.Story
import com.example.moeyslider.models.storyFactoryMock
import java.lang.RuntimeException

@Composable
fun StoryFramework(
    modifier: Modifier,
    storySet: List<List<Story>>
) {
    //LazyRow(content = storySet)
}
package com.example.moeyslider.utills

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Stable
internal fun DpSize.toPx(density: Density): Size {
    with(density) { return Size(width.toPx(), height.toPx()) }
}

@Stable
internal fun Size.toDpSize(): DpSize {
    return DpSize(width.dp,height.dp)
}
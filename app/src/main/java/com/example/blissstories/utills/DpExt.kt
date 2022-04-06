package com.example.blissstories.utills

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun abs(dp: Dp): Dp {
    return kotlin.math.abs(dp.value).dp
}

operator fun Dp.rem(other: Dp): Dp {
    return (this.value % other.value).dp
}
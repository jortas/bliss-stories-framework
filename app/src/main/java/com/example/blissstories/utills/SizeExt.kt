package com.example.blissstories.utills

import androidx.compose.animation.core.*
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
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

val DpSize.Companion.VectorConverter: TwoWayConverter<DpSize, AnimationVector2D>
    get() = DpSizeToVector

val DpSize.Companion.VisibilityThreshold: DpSize
    get() = DpSize(DpVisibilityThreshold.dp, DpVisibilityThreshold.dp)

private const val DpVisibilityThreshold = 0.1f

private val DpSizeToVector: TwoWayConverter<DpSize, AnimationVector2D> =
    TwoWayConverter(
        convertToVector = { AnimationVector2D(it.width.value, it.height.value) },
        convertFromVector = { DpSize(it.v1.dp, it.v2.dp) }
    )

@Composable
fun animateDpSIzeAsState(
    targetValue: DpSize,
    animationSpec: AnimationSpec<DpSize> = sizeDefaultSpring,
    finishedListener: ((DpSize) -> Unit)? = null
): State<DpSize> {
    return animateValueAsState(
        targetValue,
        DpSize.VectorConverter,
        animationSpec,
        finishedListener = finishedListener
    )
}

private val sizeDefaultSpring = spring(visibilityThreshold = DpSize.VisibilityThreshold)
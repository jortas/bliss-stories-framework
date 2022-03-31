package com.example.blissstories.i9stories

import androidx.compose.foundation.gestures.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.abs

internal fun Modifier.addMultipleGestures(
    density: Density,
    onGestureStart: () -> Unit = {},
    onGestureEnd: () -> Unit = {},
    onPress: (Offset) -> Unit = {},
    onLongPress: (Offset) -> Unit = {},
    onVerticalDrag: (Dp) -> Unit = {},
    onVerticalDragEnd: () -> Unit = {},
    onHorizontalDrag: (Dp) -> Unit = {},
    onHorizontalDragEnd: () -> Unit = {}
): Modifier {
    return this.pointerInput(Unit) {
        this.forEachGesture {
            awaitPointerEventScope {
                val firstTouchPointer = awaitFirstDown(requireUnconsumed = false)
                onGestureStart()
                val pressStartTime = System.currentTimeMillis()
                var pointer: PointerInputChange?
                var drag = Offset.Zero
                var totalDragWithDirection: Dp

                do {
                    pointer = awaitDragOrCancellation(firstTouchPointer.id)
                    pointer?.let {
                        drag += it.positionChangeIgnoreConsumed()
                    }
                } while (abs(drag.x) < PRESS_SAFE_ZONE && abs(drag.y) < PRESS_SAFE_ZONE && pointer != null)

                val pressedTime = System.currentTimeMillis() - pressStartTime
                if (pointer == null && pressedTime < MAX_TIME_TAP_MS) {
                    onPress(firstTouchPointer.position)
                } else if (pointer == null && pressedTime > MAX_TIME_TAP_MS) {
                    onLongPress(firstTouchPointer.position)
                } else if (pointer != null && abs(drag.x) < abs(drag.y)) {
                    totalDragWithDirection = drag.y.toDp()
                    onVerticalDrag(totalDragWithDirection)
                    do {
                        pointer = awaitVerticalDragOrCancellation(firstTouchPointer.id)
                        pointer?.let {
                            with(density) {
                                totalDragWithDirection += it.positionChange().y.toDp()
                            }
                            onVerticalDrag(totalDragWithDirection)
                        }
                    } while (pointer != null)
                    onVerticalDragEnd()
                } else if (pointer != null && abs(drag.x) >= abs(drag.y)) {
                    totalDragWithDirection = drag.x.toDp()
                    onHorizontalDrag(totalDragWithDirection)
                    do {
                        pointer = awaitHorizontalDragOrCancellation(firstTouchPointer.id)
                        pointer?.let {
                            with(density) {
                                totalDragWithDirection += it.positionChange().x.toDp()
                            }
                            onHorizontalDrag(totalDragWithDirection)
                        }
                    } while (pointer != null)
                    onHorizontalDragEnd()
                }
                onGestureEnd()
            }
        }
    }
}


internal fun getTapType(
    tapPosition: Offset?,
    width: Float
): TapType {
    if (tapPosition == null) {
        return TapType.None
    }
    val quarterOfWidth = width / 4f
    return when (tapPosition.x) {
        in 0f..quarterOfWidth -> TapType.ShortLeft
        in quarterOfWidth..(width - quarterOfWidth) -> TapType.ShortCenter
        in (width - quarterOfWidth)..width -> TapType.ShortRight
        else -> TapType.None
    }
}


enum class TapType {
    None,
    ShortLeft,
    ShortCenter,
    ShortRight,
    Long
}

private const val PRESS_SAFE_ZONE = 30f
private const val MAX_TIME_TAP_MS = 200
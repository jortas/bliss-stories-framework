package com.example.moeyslider.slider

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.moeyslider.*
import com.example.moeyslider.CorrectValueSideEffect
import com.example.moeyslider.SliderDraggableState
import com.example.moeyslider.animateToTarget
import com.example.moeyslider.snapValueToTick
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

//Values as priority over combination of steps with constructorValueRange
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    constructorValueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    values: List<Float> = valueRangeToDiscreteValues(constructorValueRange, steps),
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumbColors: BlissSliderColors.Thumb = BlissSliderColors.Defaults.thumb(),
    trackColors: BlissSliderColors.Track = BlissSliderColors.Defaults.track(),
    tickColors: BlissSliderColors.Tick = BlissSliderColors.Defaults.tick()
) {
    val onValueChangeState = rememberUpdatedState(onValueChange)

    val valueRange = remember(values) {
        if (values.isEmpty()) {
            constructorValueRange
        } else {
            values.minOrNull()!!..values.maxOrNull()!!
        }
    }

    val ticks = remember(values) {
        valuesToTickFractions(values)
    }

    BoxWithConstraints(
        modifier
            .requiredSizeIn(minWidth = ThumbRadius * 2, minHeight = ThumbRadius * 2)
            .sliderSemantics(value, ticks, enabled, onValueChange, valueRange, ticks.count())
            .focusable(enabled, interactionSource)
    ) {
        val maxPx = constraints.maxWidth.toFloat()
        val minPx = 0f

        fun scaleToUserValue(offset: Float) =
            scale(minPx, maxPx, offset, valueRange.start, valueRange.endInclusive)

        fun scaleToOffset(userValue: Float) =
            scale(valueRange.start, valueRange.endInclusive, userValue, minPx, maxPx)

        val scope = rememberCoroutineScope()
        val rawOffset = remember { mutableStateOf(scaleToOffset(value)) }
        val draggableState = remember(minPx, maxPx, valueRange) {
            SliderDraggableState {
                rawOffset.value = (rawOffset.value + it).coerceIn(minPx, maxPx)
                onValueChangeState.value.invoke(scaleToUserValue(rawOffset.value))
            }
        }

        CorrectValueSideEffect(::scaleToOffset, valueRange, rawOffset, value)

        val gestureEndAction = rememberUpdatedState<(Float) -> Unit> { velocity: Float ->
            val current = rawOffset.value
            val target = snapValueToTick(current, ticks, minPx, maxPx)
            if (current != target) {
                scope.launch {
                    animateToTarget(draggableState, current, target, velocity)
                    onValueChangeFinished?.invoke()
                }
            } else if (!draggableState.isDragging) {
                // check ifDragging in case the change is still in progress (touch -> drag case)
                onValueChangeFinished?.invoke()
            }
        }

        val press = Modifier.sliderPressModifier(
            draggableState, interactionSource, maxPx, rawOffset, gestureEndAction, enabled
        )

        val drag = Modifier.draggable(
            orientation = Orientation.Horizontal,
            enabled = enabled,
            interactionSource = interactionSource,
            onDragStopped = { velocity -> gestureEndAction.value.invoke(velocity) },
            startDragImmediately = draggableState.isDragging,
            state = draggableState
        )

        val coerced = value.coerceIn(valueRange.start, valueRange.endInclusive)
        val fraction = calcFraction(valueRange.start, valueRange.endInclusive, coerced)

        SliderImpl(
            enabled,
            fraction,
            ticks,
            BlissSliderColors(thumbColors, trackColors, tickColors),
            maxPx,
            interactionSource,
            modifier = press.then(drag)
        )
    }
}

@Composable
private fun SliderImpl(
    enabled: Boolean,
    positionFraction: Float,
    tickFractions: List<Float>,
    colors: BlissSliderColors,
    width: Float,
    interactionSource: MutableInteractionSource,
    modifier: Modifier
) {
    Box(modifier.then(DefaultSliderConstraints)) {
        val trackStrokeWidth: Float
        val tickRadius: Float
        val thumbRadius: Float
        val widthDp: Dp
        with(LocalDensity.current) {
            trackStrokeWidth = TrackHeight.toPx()
            tickRadius = TickRadius.toPx()
            thumbRadius = ThumbRadius.toPx()
            widthDp = width.toDp()
        }

        val thumbSize = ThumbRadius * 2
        val offset = (widthDp - thumbSize) * positionFraction
        val center = Modifier.align(Alignment.CenterStart)

        Track(
            center,
            colors.trackColor,
            enabled,
            0f,
            positionFraction,
            thumbRadius,
            trackStrokeWidth,
        )

        Ticks(
            modifier = center.fillMaxSize(),
            colors = colors.tickColor,
            enabled = enabled,
            positionFractionEnd = positionFraction,
            tickFractions = tickFractions,
            thumbRadius,
            tickRadius
        )

        Thumb(
            center,
            offset,
            interactionSource,
            colors.thumbColor,
            enabled,
            thumbSize
        )
    }
}

@Composable
private fun Thumb(
    modifier: Modifier,
    offset: Dp,
    interactionSource: MutableInteractionSource,
    colors: BlissSliderColors.Thumb,
    enabled: Boolean,
    thumbSize: Dp
) {
    Box(modifier.padding(start = offset)) {
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> interactions.add(interaction)
                    is PressInteraction.Release -> interactions.remove(interaction.press)
                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
                    is DragInteraction.Start -> interactions.add(interaction)
                    is DragInteraction.Stop -> interactions.remove(interaction.start)
                    is DragInteraction.Cancel -> interactions.remove(interaction.start)
                }
            }
        }

        val elevation = if (interactions.isNotEmpty()) {
            ThumbPressedElevation
        } else {
            ThumbDefaultElevation
        }

        Box(
            Modifier
                .size(thumbSize, thumbSize)
                .indication(
                    interactionSource = interactionSource,
                    indication = rememberRipple(bounded = false, radius = ThumbRippleRadius)
                )
                .shadow(if (enabled) elevation else 0.dp, CircleShape, clip = false)
                .background(Color.White, CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.thumbColor(enabled).value)
                    .align(Alignment.Center)
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun Track(
    modifier: Modifier,
    colors: BlissSliderColors.Track,
    enabled: Boolean,
    positionFractionStart: Float,
    positionFractionEnd: Float,
    thumbRadius: Float,
    trackStrokeWidth: Float,
) {

    val activeTrackBrush = colors.brush(enabled, active = true)
    val inactiveTrackBrush = colors.brush(enabled, active = false)
    Canvas(modifier.fillMaxSize()) {
        val sliderLeft = Offset(thumbRadius, center.y)
        val sliderRight = Offset(size.width - thumbRadius, center.y)

        drawLine(
            inactiveTrackBrush.value,
            sliderLeft,
            sliderRight,
            trackStrokeWidth,
            StrokeCap.Round
        )
    }

    val theDp = with(LocalDensity.current) {
        thumbRadius.toDp()
    }

    Canvas(
        modifier
            .padding(start = theDp, end = theDp)
            .fillMaxWidth(fraction = positionFractionEnd)
    ) {
        val sliderLeft = Offset(0f, center.y)
        val sliderRight = Offset(size.width, center.y)
        drawLine(
            activeTrackBrush.value,
            sliderLeft,
            sliderRight,
            trackStrokeWidth,
            StrokeCap.Round,
        )
    }
}


@Composable
private fun Ticks(
    modifier: Modifier,
    colors: BlissSliderColors.Tick,
    enabled: Boolean,
    positionFractionEnd: Float,
    tickFractions: List<Float>,
    thumbRadius: Float,
    tickRadius: Float
) {

    val inactiveColor = colors.color(enabled, active = false)
    val activeColor = colors.color(enabled, active = true)

    Canvas(modifier) {
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val sliderLeft = Offset(thumbRadius, center.y)
        val sliderRight = Offset(size.width - thumbRadius, center.y)
        val sliderStart = if (isRtl) sliderRight else sliderLeft
        val sliderEnd = if (isRtl) sliderLeft else sliderRight

        tickFractions.groupBy { it > positionFractionEnd }.forEach { (afterFraction, list) ->
            drawPoints(
                points = list.map {
                    Offset(lerp(sliderStart, sliderEnd, it).x, center.y)
                },
                pointMode = PointMode.Points,
                color = (if (afterFraction) inactiveColor else activeColor).value,
                tickRadius * 2,
                StrokeCap.Round
            )
        }
    }
}

private fun stepsToTickFractions(steps: Int): List<Float> {
    return if (steps == 0) emptyList() else List(steps + 2) { it.toFloat() / (steps + 1) }
}

private fun valueRangeToDiscreteValues(
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0
): List<Float> {
    return stepsToTickFractions(steps).map { (it - valueRange.start) / (valueRange.endInclusive - valueRange.start) }
}

private fun valuesToTickFractions(values: List<Float>): List<Float> {
    val min = values.minOrNull()
    return values.maxOrNull()?.let { max ->
        values.map { (it - min!!) / (max - min) }
    } ?: emptyList()
}

// Scale x1 from a1..b1 range to a2..b2 range
private fun scale(a1: Float, b1: Float, x1: Float, a2: Float, b2: Float) =
    lerp(a2, b2, calcFraction(a1, b1, x1))

// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)

// Internal to be referred to in tests
internal val ThumbRadius = 18.dp
private val ThumbRippleRadius = 24.dp
private val ThumbDefaultElevation = 1.dp
private val ThumbPressedElevation = 6.dp

// Internal to be referred to in tests
internal val TrackHeight = 24.dp
private val SliderHeight = 48.dp
private val SliderMinWidth = 144.dp // TODO: clarify min width
private val DefaultSliderConstraints =
    Modifier
        .widthIn(min = SliderMinWidth)
        .heightIn(max = SliderHeight)

internal val TickRadius = 4.dp

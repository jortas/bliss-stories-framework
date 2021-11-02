package com.example.moeyslider.slider

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver


@Immutable
data class BlissSliderColors(
    val thumbColor: Thumb,
    val trackColor: Track,
    val tickColor: Tick
) {

    @Immutable
    data class Thumb(
        val color: Color,
        val disabledColor: Color,
    ) {
        @Composable
        fun thumbColor(enabled: Boolean): State<Color> {
            return rememberUpdatedState(if (enabled) color else disabledColor)
        }
    }

    //Brush have priority over color
    @Immutable
    data class Track(
        val activeColor: Color = Color.Unspecified,
        val inactiveColor: Color = Color.Unspecified,
        val disabledActiveColor: Color = Color.Unspecified,
        val disabledInactiveColor: Color = Color.Unspecified,

        val activeBrush: Brush = SolidColor(activeColor),
        val inactiveBrush: Brush = SolidColor(inactiveColor),
        val disabledActiveBrush: Brush = SolidColor(disabledActiveColor),
        val disabledInactiveBrush: Brush = SolidColor(disabledInactiveColor)
    ) {

        @Composable
        fun brush(enabled: Boolean, active: Boolean): State<Brush> {
            return rememberUpdatedState(
                if (enabled) {
                    if (active) activeBrush else inactiveBrush
                } else {
                    if (active) disabledActiveBrush else disabledInactiveBrush
                }
            )
        }
    }

    @Immutable
    data class Tick(
        val activeColor: Color,
        val inactiveColor: Color,
        val disabledActiveColor: Color,
        val disabledInactiveColor: Color
    ) {

        @Composable
        fun color(enabled: Boolean, active: Boolean): State<Color> {
            return rememberUpdatedState(
                if (enabled) {
                    if (active) activeColor else inactiveColor
                } else {
                    if (active) disabledActiveColor else disabledInactiveColor
                }
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BlissSliderColors

        if (thumbColor != other.thumbColor) return false
        if (trackColor != other.trackColor) return false
        if (tickColor != other.tickColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thumbColor.hashCode()
        result = 31 * result + trackColor.hashCode()
        result = 31 * result + tickColor.hashCode()
        return result
    }


    object Defaults {
        @Composable
        fun thumb(
            color: Color = MaterialTheme.colors.primary,
            disabledColor: Color = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.disabled)
                .compositeOver(MaterialTheme.colors.surface)
        ): BlissSliderColors.Thumb = BlissSliderColors.Thumb(color, disabledColor)

        @Composable
        fun track(
            activeColor: Color = MaterialTheme.colors.primary,
            inactiveColor: Color = activeColor.copy(alpha = InactiveTrackAlpha),
            disabledActiveColor: Color =
                MaterialTheme.colors.onSurface.copy(alpha = DisabledActiveTrackAlpha),
            disabledInactiveColor: Color = disabledActiveColor.copy(alpha = DisabledInactiveTrackAlpha),

            activeBrush: Brush = SolidColor(activeColor),
            inactiveBrush: Brush = SolidColor(inactiveColor),
            disabledActiveBrush: Brush = SolidColor(disabledActiveColor),
            disabledInactiveBrush: Brush = SolidColor(disabledInactiveColor)
        ): BlissSliderColors.Track = BlissSliderColors.Track(
            activeBrush = activeBrush,
            inactiveBrush = inactiveBrush,
            disabledActiveBrush = disabledActiveBrush,
            disabledInactiveBrush = disabledInactiveBrush
        )

        @Composable
        fun tick(
            activeColor: Color = MaterialTheme.colors.primary.copy(alpha = TickAlpha),
            inactiveColor: Color = activeColor.copy(alpha = TickAlpha),
            disabledActiveColor: Color = activeColor.copy(alpha = DisabledTickAlpha),
            disabledInactiveColor: Color = activeColor.copy(alpha = DisabledTickAlpha)
        ): BlissSliderColors.Tick = BlissSliderColors.Tick(
            activeColor,
            inactiveColor,
            disabledActiveColor,
            disabledInactiveColor
        )

        /**
         * Default alpha of the inactive part of the track
         */
        private const val InactiveTrackAlpha = 0.24f

        /**
         * Default alpha for the track when it is disabled but active
         */
        private const val DisabledInactiveTrackAlpha = 0.12f

        /**
         * Default alpha for the track when it is disabled and inactive
         */
        private const val DisabledActiveTrackAlpha = 0.32f

        /**
         * Default alpha of the ticks that are drawn on top of the track
         */
        private const val TickAlpha = 0.54f

        /**
         * Default alpha for tick marks when they are disabled
         */
        private const val DisabledTickAlpha = 0.12f
    }
}
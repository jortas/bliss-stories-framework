package com.example.blissstories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.blissstories.R

@Immutable
object AppFontFamilies {
    @Stable
    val Gloriola = FontFamily(
        Font(resId = R.font.gloriola_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(resId = R.font.gloriola_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
        Font(resId = R.font.gloriola_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
        Font(resId = R.font.gloriola_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    )
}

@Immutable
data class Typography(
    val headingMedium: TextStyle,
    val captionLarge: TextStyle
)

fun typography(): Typography {
    val baseTextStyle = TextStyle(
        fontFamily = AppFontFamilies.Gloriola,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.04.em,
        color = Color.White
    )

    return Typography(
        headingMedium = baseTextStyle.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 28.sp,
            lineHeight = 42.sp,
            letterSpacing = 0.02.em
        ),
        captionLarge = baseTextStyle.copy(
            fontSize = 14.sp
        ),
    )
}

@Composable
internal fun rememberTypography(): Typography {
    return remember() { typography() }
}
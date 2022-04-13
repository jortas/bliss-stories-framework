@file:Suppress("NOTHING_TO_INLINE")

package com.example.blissstories.projectutils

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.blissstories.projectutils.resources.ColorKeyResource
import com.example.blissstories.projectutils.resources.ImageKeyResource
import java.util.*


inline fun ResourceProvider.imageKey(resource: ImageKeyResource): Drawable? {
    val imageKey = resource.key
    val id = context.resources.getIdentifier("ic_$imageKey", "drawable", context.packageName)
    return if (id != 0) drawable(id) else null
}

inline fun ResourceProvider.colorKey(resource: ColorKeyResource): Int? {
    val colorKey = resource.key
    val colorKeyHandled = handleColorName(colorKey)
    val id =
        context.resources.getIdentifier("$colorKeyHandled", "color", context.packageName)
    return if (id != 0) color(id) else null
}

@Composable
inline fun ResourceProvider.colorComposeKey(resource: ColorKeyResource): Color? {
    val colorKey = resource.key
    val colorKeyHandled = handleColorName(colorKey)
    val id =
        context.resources.getIdentifier(colorKeyHandled, "color", context.packageName)
    return if (id != 0) colorResource(id) else null
}

inline fun ResourceProvider.handleColorName(colorKey: String): String {
    return colorKey.replace("(.)([A-Z0-9]\\w)".toRegex(), "$1_$2")
        .lowercase(Locale.getDefault())
}
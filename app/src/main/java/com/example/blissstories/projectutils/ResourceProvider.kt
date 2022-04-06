package com.example.blissstories.projectutils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat

@Suppress("NOTHING_TO_INLINE")
inline class ResourceProvider(val context: Context) {

    inline fun text(@StringRes resId: Int): CharSequence = context.getText(resId)

    inline fun textArray(@ArrayRes resId: Int): Array<out CharSequence> =
        context.resources.getTextArray(resId)

    inline fun quantityText(@PluralsRes resId: Int, quantity: Int): CharSequence =
        context.resources.getQuantityText(resId, quantity)

    inline fun string(@StringRes resId: Int): String = context.getString(resId)

    inline fun string(@StringRes resId: Int, vararg formatArgs: Any?): String =
        context.getString(resId, *formatArgs)

    inline fun stringArray(@ArrayRes resId: Int): Array<out String> =
        context.resources.getStringArray(resId)

    inline fun quantityString(@PluralsRes resId: Int, quantity: Int): String =
        context.resources.getQuantityString(resId, quantity)

    inline fun quantityString(
        @PluralsRes resId: Int, quantity: Int,
        vararg formatArgs: Any?
    ): String =
        context.resources.getQuantityString(resId, quantity, *formatArgs)

    inline fun integer(@IntegerRes resId: Int) = context.resources.getInteger(resId)

    inline fun intArray(@ArrayRes resId: Int): IntArray = context.resources.getIntArray(resId)

    inline fun boolean(@BoolRes resId: Int) = context.resources.getBoolean(resId)

    inline fun float(@DimenRes resId: Int) = ResourcesCompat.getFloat(context.resources, resId)

    inline fun dimension(@DimenRes resId: Int) = context.resources.getDimension(resId)

    inline fun dimensionPixelSize(@DimenRes resId: Int) =
        context.resources.getDimensionPixelSize(resId)

    inline fun dimensionPixelOffset(@DimenRes resId: Int) =
        context.resources.getDimensionPixelOffset(resId)

    inline fun drawable(@DrawableRes resId: Int): Drawable? =
        AppCompatResources.getDrawable(context, resId)

    @ColorInt
    inline fun color(@ColorRes resId: Int) =
        AppCompatResources.getColorStateList(context, resId).defaultColor

    @Throws(Resources.NotFoundException::class)
    inline fun colorStateList(@ColorRes resId: Int): ColorStateList? =
        AppCompatResources.getColorStateList(context, resId)

    @Throws(Resources.NotFoundException::class)
    inline fun font(@FontRes id: Int): Typeface? = ResourcesCompat.getFont(context, id)

    @Throws(Resources.NotFoundException::class)
    inline fun animation(@AnimRes id: Int): Animation = AnimationUtils.loadAnimation(context, id)
}

fun ResourceProvider.getNavigationBarHeight(): Int {
    val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        context.resources.getDimensionPixelSize(resourceId)
    } else 0
}

fun ResourceProvider.getStatusBarHeight(): Int {
    val resourceId: Int = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        context.resources.getDimensionPixelSize(resourceId)
    } else 0
}

inline val Context.resourceProvider: ResourceProvider
    get() = ResourceProvider(this)

inline val View.resourceProvider: ResourceProvider
    get() = ResourceProvider(context)
package com.example.moeyslider

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.MotionEvent
import android.widget.ImageView


@SuppressLint("ClickableViewAccessibility")
class Slider : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var thumbPosition = 0f
    var sliderProgress: ImageView
    var originalX: Float

    init {
        inflate(R.layout.slider, true)
        sliderProgress = this.findViewById(R.id.sliderProgress)
        originalX = sliderProgress.x



        this.findViewById<View>(R.id.thumb).setOnTouchListener { view, event ->
            if (event == null) {
                false
            }

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    thumbPosition = view.x - event.rawX
                }
                MotionEvent.ACTION_MOVE -> {
                    view.animate()
                        .x(event.rawX + thumbPosition)
                        .setDuration(0)
                        .start()

                    sliderProgress.animate()
                        .scaleX( (event.rawX + thumbPosition))
                        .x(originalX + event.rawX /2)
                        .start()
                }
                    else -> false
            }
            true
        }

    }

}

fun ViewGroup.inflate(resId: Int, attachToRoot: Boolean = false): View =
    context.layoutInflater.inflate(resId, this, attachToRoot)


inline val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)
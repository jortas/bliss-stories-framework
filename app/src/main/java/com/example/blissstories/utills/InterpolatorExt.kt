package com.example.blissstories.utills

import androidx.compose.ui.unit.Dp
import com.example.blissstories.i9stories.ui.abs
import com.example.blissstories.i9stories.ui.rem
import kotlin.math.sign
import kotlin.math.tanh

//Interpolator hyperbolic tangent
//Starts linear from initial value and ends tending to the maximum value never reaching it
fun hyperbolicTangentInterpolator(value: Float, initialValue: Float, limitValue: Float): Float {
    val interval = limitValue - initialValue
    if (interval == 0f){
        return initialValue
    }
    return interval.sign * tanh(value / (interval)) * (interval) + initialValue
}


//Cyclic interpolation of a value starting in max going to min in the middle of the cycle
// and coming back to max in the end of the cycle
// MIN + | x % w - w/2| / (w/2) (MAX - MIN)
fun middleMinInterpolation(
    x: Dp,
    intervalCycle: Dp,
    minValue: Float,
    maxValue: Float
): Float {
    return minValue + (abs(abs(x) % intervalCycle - intervalCycle / 2) / (intervalCycle / 2) * (maxValue - minValue))
}
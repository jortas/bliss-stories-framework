package com.example.blissstories.i9stories

import androidx.compose.ui.unit.dp
import junit.framework.TestCase

class StorySetsPlayerKtTest : TestCase() {
    val width = 500.dp
    fun test0() {
        val result = middleMinInterpolation(0.dp, width, 0.9f, 1f)
        assertTrue(result == 1f)
    }

    fun test250() {
        val result = middleMinInterpolation(250.dp, width, 0.9f, 1f)
        assertTrue(result == 0.9f)
    }

    fun test500() {
        val result = middleMinInterpolation(500.dp, width, 0.9f, 1f)
        assertTrue(result == 1f)
    }

    fun test125() {
        val result = middleMinInterpolation(125.dp, width, 0.9f, 1f)
        assertTrue(result == 0.95f)
    }
}
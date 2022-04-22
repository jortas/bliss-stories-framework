package com.example.blissstories.i9stories.ui.components

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    @IntRange(from = 0) numberOfSteps: Int,
    @IntRange(from = 0) currentStep: Int,
    @FloatRange(from = 0.0, to = 1.0) stepProgress: Float
) {
    if (currentStep >= numberOfSteps) {
        throw RuntimeException("CurrentStory Index $currentStep bigger than $numberOfSteps")
    }

    Row(
        modifier
            .fillMaxWidth()
            .height(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 0 until numberOfSteps) {
            val progress = when (i) {
                in 0 until currentStep -> 1f
                currentStep -> stepProgress
                else -> 0f
            }

            ProgressStep(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                progress = progress
            )
        }
    }
}

@Preview
@Composable
private fun ComposedStoryProgressPreview() {
    MaterialTheme {
        ProgressBar(
            numberOfSteps = 4,
            currentStep = 2,
            stepProgress = 0.7f
        )
    }
}
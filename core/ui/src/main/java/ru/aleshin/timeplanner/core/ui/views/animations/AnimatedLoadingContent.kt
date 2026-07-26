/*
 * Copyright 2025 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.aleshin.timeplanner.core.ui.views.animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * @author Stanislav Aleshin on 07.01.2026.
 */
@Composable
fun AnimatedLoadingContent(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    minDisplayTime: Long = 500L,
    label: String = "LoadingTransition",
    content: @Composable AnimatedContentScope.(loading: Boolean) -> Unit,
) {
    val smoothedLoading by rememberSmoothedLoading(isLoading, minDisplayTime)

    AnimatedContent(
        modifier = modifier,
        targetState = smoothedLoading,
        transitionSpec = {
            fadeIn(animationSpec = tween(FADE_DURATION)) togetherWith
                    fadeOut(animationSpec = tween(FADE_DURATION))
        },
        contentAlignment = contentAlignment,
        label = label,
        contentKey = { it },
        content = content,
    )
}

@Composable
fun <S> AnimatedLoadingContent(
    isLoading: Boolean,
    targetValue: S?,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    minDisplayTime: Long = 500L,
    label: String = "LoadingTransition",
    content: @Composable AnimatedContentScope.(S?) -> Unit,
) {
    val smoothedLoading by rememberSmoothedLoading(isLoading, minDisplayTime)

    AnimatedContent(
        modifier = modifier,
        targetState = if (smoothedLoading) null else targetValue,
        transitionSpec = {
            fadeIn(animationSpec = tween(FADE_DURATION)) togetherWith
                    fadeOut(animationSpec = tween(FADE_DURATION))
        },
        contentAlignment = contentAlignment,
        label = label,
        contentKey = { it == null },
        content = content,
    )
}

@Composable
@OptIn(ExperimentalTime::class)
fun rememberSmoothedLoading(
    isLoading: Boolean,
    minDuration: Long,
): State<Boolean> {
    val retainedLoading = remember { mutableStateOf(isLoading) }
    val lastStartTime = remember { mutableLongStateOf(0L) }
    val smoothedLoading = remember(isLoading) {
        derivedStateOf { isLoading || retainedLoading.value }
    }

    LaunchedEffect(isLoading, minDuration) {
        if (isLoading) {
            lastStartTime.longValue = Clock.System.now().toEpochMilliseconds()
            retainedLoading.value = true
        } else if (retainedLoading.value) {
            val elapsed = Clock.System.now().toEpochMilliseconds() - lastStartTime.longValue
            val remaining = minDuration - elapsed
            if (remaining > 0) {
                delay(remaining)
            }
            retainedLoading.value = false
        }
    }
    return smoothedLoading
}

const val FADE_DURATION = 500

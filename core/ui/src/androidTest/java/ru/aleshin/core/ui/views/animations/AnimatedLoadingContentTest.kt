/*
 * Copyright 2026 Stanislav Aleshin
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

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
internal class AnimatedLoadingContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loadingStartsWithoutIntermediateContentFrame() {
        val renderedStates = mutableListOf<Boolean>()
        lateinit var updateLoading: (Boolean) -> Unit

        composeRule.setContent {
            var isLoading by remember { mutableStateOf(false) }
            updateLoading = { isLoading = it }
            val smoothedLoading by rememberSmoothedLoading(
                isLoading = isLoading,
                minDuration = 0L,
            )

            SideEffect {
                renderedStates.add(smoothedLoading)
            }
        }
        composeRule.runOnIdle {
            renderedStates.clear()
            updateLoading(true)
        }
        composeRule.waitForIdle()

        assertEquals(listOf(true), renderedStates)
    }

    @Test
    fun loadingTransitionKeepsPreviousContentSnapshot() {
        val renderedContent = mutableListOf<String>()
        lateinit var updateState: (Pair<Boolean, String?>) -> Unit

        composeRule.setContent {
            var state by remember {
                mutableStateOf<Pair<Boolean, String?>>(false to "content")
            }
            updateState = { state = it }

            AnimatedLoadingContent(
                isLoading = state.first,
                targetValue = state,
                minDisplayTime = 0L,
            ) { contentState ->
                SideEffect {
                    renderedContent.add(
                        when {
                            contentState == null -> "placeholder"
                            contentState.second == null -> "empty"
                            else -> contentState.second.orEmpty()
                        },
                    )
                }
            }
        }
        composeRule.runOnIdle {
            renderedContent.clear()
            updateState(true to null)
        }
        composeRule.waitForIdle()

        assertFalse(renderedContent.contains("empty"))
        assertTrue(renderedContent.contains("placeholder"))
    }
}

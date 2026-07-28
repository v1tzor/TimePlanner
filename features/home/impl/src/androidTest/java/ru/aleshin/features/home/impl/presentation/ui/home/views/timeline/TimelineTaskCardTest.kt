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
package ru.aleshin.features.home.impl.presentation.ui.home.views.timeline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.down
import androidx.compose.ui.test.moveBy
import androidx.compose.ui.test.up
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.impl.presentation.models.TimelineTimeTaskUi
import ru.aleshin.features.home.impl.presentation.theme.tokens.HomeCategoryColors
import ru.aleshin.features.home.impl.presentation.theme.tokens.LocalHomeIcons
import ru.aleshin.features.home.impl.presentation.theme.tokens.LocalHomeStrings
import ru.aleshin.features.home.impl.presentation.theme.tokens.baseHomeIcons
import ru.aleshin.features.home.impl.presentation.theme.tokens.englishHomeString
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
internal class TimelineTaskCardTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun moveDragUsesLatestCallbacksAfterModelRecomposition() {
        val date = date()
        val originalRange = TimeRange(date.at(8, 30), date.at(15, 45))
        val updatedRange = TimeRange(date.at(8, 30), date.at(8, 35))
        lateinit var updateRange: (TimeRange) -> Unit
        var rangeAtDragStart: TimeRange? = null
        var rangeAtDrag: TimeRange? = null

        composeRule.setContent {
            var timeRange by remember { mutableStateOf(originalRange) }
            updateRange = { range -> timeRange = range }
            val callbackRange = timeRange

            CompositionLocalProvider(
                LocalHomeStrings provides englishHomeString,
                LocalHomeIcons provides baseHomeIcons,
            ) {
                MaterialTheme {
                    TimelineTaskCard(
                        modifier = Modifier.size(width = 320.dp, height = 80.dp),
                        model = timeTask(date, timeRange),
                        timeRange = timeRange,
                        colors = HomeCategoryColors(
                            accent = Color.Blue,
                            container = Color.LightGray,
                        ),
                        isSelected = false,
                        isDragging = false,
                        onClick = {},
                        onMoveClick = {},
                        onEditModeCancel = {},
                        onDoneChange = {},
                        onDragStart = { mode ->
                            rangeAtDragStart = callbackRange
                            mode == TimelineTaskDragMode.MOVE
                        },
                        onDrag = { rangeAtDrag = callbackRange },
                        onDragEnd = {},
                        onDragCancel = {},
                    )
                }
            }
        }
        composeRule.runOnIdle { updateRange(updatedRange) }
        composeRule.waitForIdle()

        composeRule
            .onNodeWithContentDescription(englishHomeString.timelineTaskMoveIconDesc)
            .performTouchInput {
                down(center)
                moveBy(Offset(x = 0f, y = 80f))
                up()
            }

        composeRule.runOnIdle {
            assertEquals(updatedRange, rangeAtDragStart)
            assertEquals(updatedRange, rangeAtDrag)
        }
    }

    @Test
    fun moveDragIsNotInterceptedByParentVerticalScroll() {
        val date = date()
        val timeRange = TimeRange(date.at(8, 30), date.at(9, 30))
        var parentScrollValue = -1
        var totalDragAmount = 0f

        composeRule.setContent {
            val scrollState = rememberScrollState()
            parentScrollValue = scrollState.value

            CompositionLocalProvider(
                LocalHomeStrings provides englishHomeString,
                LocalHomeIcons provides baseHomeIcons,
            ) {
                MaterialTheme {
                    Column(
                        modifier = Modifier
                            .size(width = 320.dp, height = 300.dp)
                            .verticalScroll(scrollState),
                    ) {
                        Spacer(modifier = Modifier.height(180.dp))
                        TimelineTaskCard(
                            modifier = Modifier.size(width = 320.dp, height = 80.dp),
                            model = timeTask(date, timeRange),
                            timeRange = timeRange,
                            colors = HomeCategoryColors(
                                accent = Color.Blue,
                                container = Color.LightGray,
                            ),
                            isSelected = false,
                            isDragging = false,
                            onClick = {},
                            onMoveClick = {},
                            onEditModeCancel = {},
                            onDoneChange = {},
                            onDragStart = { mode -> mode == TimelineTaskDragMode.MOVE },
                            onDrag = { dragAmount -> totalDragAmount += dragAmount },
                            onDragEnd = {},
                            onDragCancel = {},
                        )
                        Spacer(modifier = Modifier.height(500.dp))
                    }
                }
            }
        }

        composeRule
            .onNodeWithContentDescription(englishHomeString.timelineTaskMoveIconDesc)
            .performTouchInput {
                down(center)
                moveBy(Offset(x = 0f, y = -80f))
                up()
            }

        composeRule.runOnIdle {
            assertTrue(totalDragAmount < 0f)
            assertEquals(0, parentScrollValue)
        }
    }

    private fun timeTask(
        date: Date,
        timeRange: TimeRange,
    ): TimelineTimeTaskUi {
        val task = TimeTaskUi(
            key = 1L,
            date = date,
            timeRanges = timeRange,
            category = MainCategoryUi(
                id = 1L,
                customName = "University",
                defaultType = null,
            ),
        )
        return TimelineTimeTaskUi(
            timeTask = task,
            executionStatus = TimeTaskStatus.PLANNED,
            visibleTimeRange = timeRange,
            minimumStartTime = date,
            maximumEndTime = date.shiftDay(),
            canMove = true,
            canResizeStart = false,
            canResizeEnd = false,
        )
    }

    private fun date(): Date {
        return Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JULY, 17)
        }.time
    }

    private fun Date.at(hour: Int, minute: Int): Date {
        return Calendar.getInstance().apply {
            time = this@at
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }.time
    }

    private fun Date.shiftDay(): Date {
        return Calendar.getInstance().apply {
            time = this@shiftDay
            add(Calendar.DAY_OF_YEAR, 1)
        }.time
    }
}

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
package ru.aleshin.timeplanner.widgets.domain.interactors

import kotlinx.coroutines.flow.first
import ru.aleshin.core.domain.common.GoalProgressManager
import ru.aleshin.core.domain.common.RecurringScheduleManager
import ru.aleshin.core.domain.common.TimeTaskStatusChecker
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.repository.GoalRepository
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TasksSettingsRepository
import ru.aleshin.core.domain.repository.ThemeSettingsRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.domain.repository.UndefinedTaskRepository
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.timeplanner.widgets.domain.common.WidgetEitherWrapper
import ru.aleshin.timeplanner.widgets.domain.entities.WidgetFailure
import ru.aleshin.timeplanner.widgets.domain.entities.snapshot.WidgetsSnapshot
import ru.aleshin.timeplanner.widgets.domain.entities.tasks.WidgetTimeTask
import ru.aleshin.timeplanner.widgets.domain.managers.DailySummaryCalculator
import ru.aleshin.timeplanner.widgets.domain.managers.DeadlineTasksCalculator
import ru.aleshin.timeplanner.widgets.domain.managers.WeekOverviewCalculator
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
interface WidgetsInteractor {

    suspend fun fetchSnapshot(): DomainResult<WidgetFailure, WidgetsSnapshot>

    class Base @Inject constructor(
        private val scheduleRepository: ScheduleRepository,
        private val timeTaskRepository: TimeTaskRepository,
        private val undefinedTaskRepository: UndefinedTaskRepository,
        private val goalRepository: GoalRepository,
        private val themeSettingsRepository: ThemeSettingsRepository,
        private val tasksSettingsRepository: TasksSettingsRepository,
        private val recurringScheduleManager: RecurringScheduleManager,
        private val timeTaskStatusChecker: TimeTaskStatusChecker,
        private val weekOverviewCalculator: WeekOverviewCalculator,
        private val dailySummaryCalculator: DailySummaryCalculator,
        private val deadlineTasksCalculator: DeadlineTasksCalculator,
        private val goalProgressManager: GoalProgressManager,
        private val dateManager: DateManager,
        private val eitherWrapper: WidgetEitherWrapper,
    ) : WidgetsInteractor {

        override suspend fun fetchSnapshot() = eitherWrapper.wrap {
            val currentTime = dateManager.fetchCurrentDate()
            val currentDay = currentTime.startThisDay()

            val targetDates = List(WEEK_DAYS_COUNT) { currentDay.shiftDay(it) }
            val targetTimeRange = TimeRange(from = targetDates.first(), to = targetDates.last())

            recurringScheduleManager.createMissingSchedules(targetDates)

            val schedules = scheduleRepository.fetchSchedulesByRange(targetTimeRange).first()
            val undefinedTasks = undefinedTaskRepository.fetchUndefinedTasks().first()
            val goals = goalRepository.fetchAllGoals().first()
            val goalTaskSourceRange = goalProgressManager.fetchTaskSourceRange(goals, currentTime)
            val goalTasks = goalTaskSourceRange?.let { sourceRange ->
                timeTaskRepository.fetchTimeTasksByScheduleDateRange(sourceRange).first()
            }.orEmpty()
            val themeSettings = themeSettingsRepository.fetchSettingsOnce()
            val tasksSettings = tasksSettingsRepository.fetchSettings().first()
            val weekOverview = weekOverviewCalculator.calculate(targetDates, schedules)
            val sourceTodayTasks = weekOverview.days.firstOrNull()?.tasks.orEmpty()
            val todayTasks = sourceTodayTasks.map { task ->
                WidgetTimeTask(task, timeTaskStatusChecker.fetchStatus(task.timeRange))
            }
            val deadlines = deadlineTasksCalculator.calculate(undefinedTasks, currentTime)
            val goalsProgress = goalProgressManager.calculate(goals, goalTasks, currentTime)
            val dailySummary = dailySummaryCalculator.calculate(sourceTodayTasks, currentTime)

            WidgetsSnapshot(
                generatedAt = currentTime,
                themeSettings = themeSettings,
                secureMode = tasksSettings.secureMode,
                todayTasks = todayTasks,
                deadlines = deadlines,
                goals = goalsProgress,
                weekOverview = weekOverview,
                dailySummary = dailySummary,
                nextUpdateAt = fetchNextUpdateAt(
                    currentTime = currentTime,
                    currentDayTasks = sourceTodayTasks,
                    nearestDeadline = deadlines.nearestDeadline,
                ),
            )
        }

        private fun fetchNextUpdateAt(
            currentTime: Date,
            currentDayTasks: List<TimeTask>,
            nearestDeadline: Date?,
        ): Date {
            val taskBoundaries = currentDayTasks.flatMap { task ->
                listOf(task.timeRange.from, task.timeRange.to)
            }
            val daySequence = sequenceOf(
                taskBoundaries.asSequence(),
                listOfNotNull(nearestDeadline).asSequence(),
                sequenceOf(currentTime.startThisDay().shiftDay(1)),
            )

            return daySequence
                .flatten()
                .filter { it.time > currentTime.time }
                .minByOrNull { it.time } ?: currentTime.startThisDay().shiftDay(1)
        }
    }
}

private const val WEEK_DAYS_COUNT = 7

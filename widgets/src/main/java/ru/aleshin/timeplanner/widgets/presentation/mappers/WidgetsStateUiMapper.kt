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
package ru.aleshin.timeplanner.widgets.presentation.mappers

import android.content.Context
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.tasks.UndefinedTask
import ru.aleshin.core.presentation.mappers.mapToString
import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.core.utils.extensions.fetchLocale
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreLanguage
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreStrings
import ru.aleshin.timeplanner.widgets.R
import ru.aleshin.timeplanner.widgets.domain.entities.analytics.WidgetWeekDay
import ru.aleshin.timeplanner.widgets.domain.entities.deadlines.WidgetDeadlineTask
import ru.aleshin.timeplanner.widgets.domain.entities.deadlines.WidgetDeadlineType
import ru.aleshin.timeplanner.widgets.domain.entities.snapshot.WidgetsSnapshot
import ru.aleshin.timeplanner.widgets.domain.entities.tasks.WidgetTimeTask
import ru.aleshin.timeplanner.widgets.presentation.models.WidgetTaskUi
import ru.aleshin.timeplanner.widgets.presentation.models.WidgetThemeUi
import ru.aleshin.timeplanner.widgets.presentation.models.WidgetUndefinedTaskUi
import ru.aleshin.timeplanner.widgets.presentation.models.WidgetWeekDayUi
import ru.aleshin.timeplanner.widgets.presentation.theme.fetchWidgetLanguage
import ru.aleshin.timeplanner.widgets.presentation.theme.fetchWidgetLocale
import ru.aleshin.timeplanner.widgets.presentation.theme.fetchWidgetString
import ru.aleshin.timeplanner.widgets.presentation.ui.deadlines.state.DeadlinesWidgetStateUi
import ru.aleshin.timeplanner.widgets.presentation.ui.summary.state.DailySummaryWidgetStateUi
import ru.aleshin.timeplanner.widgets.presentation.ui.today.state.TodayWidgetStateUi
import ru.aleshin.timeplanner.widgets.presentation.ui.week.state.WeekOverviewWidgetStateUi
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.max

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class WidgetsStateUiMapper @Inject constructor(
    private val context: Context,
) {

    fun mapTheme(snapshot: WidgetsSnapshot): WidgetThemeUi {
        val settings = snapshot.themeSettings
        return WidgetThemeUi(
            language = fetchWidgetLanguage(settings.language.code ?: context.fetchLocale().language),
            theme = settings.themeColors.mapToUi(),
        )
    }

    fun mapToday(snapshot: WidgetsSnapshot): TodayWidgetStateUi {
        val language = snapshot.themeSettings.language.code ?: context.fetchLocale().language
        return TodayWidgetStateUi(
            updatedAt = snapshot.generatedAt.time,
            tasks = snapshot.todayTasks.map { task ->
                task.mapToUi(language, snapshot.secureMode)
            },
        )
    }

    fun mapDeadlines(snapshot: WidgetsSnapshot): DeadlinesWidgetStateUi {
        val language = snapshot.themeSettings.language.code ?: context.fetchLocale().language
        return DeadlinesWidgetStateUi(
            updatedAt = snapshot.generatedAt.time,
            tasks = snapshot.deadlines.tasks.map { task ->
                task.mapToUi(snapshot.generatedAt, language, snapshot.secureMode)
            },
            overdueCount = snapshot.deadlines.overdueCount,
            todayCount = snapshot.deadlines.todayCount,
            upcomingCount = snapshot.deadlines.upcomingCount,
        )
    }

    fun mapWeek(snapshot: WidgetsSnapshot): WeekOverviewWidgetStateUi {
        val language = snapshot.themeSettings.language.code ?: context.fetchLocale().language
        val locale = fetchWidgetLocale(language)
        val formatter = SimpleDateFormat("EEE, d MMM", locale)
        return WeekOverviewWidgetStateUi(
            updatedAt = snapshot.generatedAt.time,
            tasksCount = snapshot.weekOverview.tasksCount,
            totalWorkload = snapshot.weekOverview.totalWorkload,
            busiestDayTitle = snapshot.weekOverview.busiestDay?.let(formatter::format),
            days = snapshot.weekOverview.days.map { day ->
                day.mapToUi(snapshot.generatedAt, locale)
            },
        )
    }

    fun mapSummary(snapshot: WidgetsSnapshot): DailySummaryWidgetStateUi {
        return DailySummaryWidgetStateUi(
            updatedAt = snapshot.generatedAt.time,
            completedCount = snapshot.dailySummary.completedCount,
            skippedCount = snapshot.dailySummary.skippedCount,
            remainingCount = snapshot.dailySummary.remainingCount,
            allCount = snapshot.dailySummary.allCount,
            plannedDuration = snapshot.dailySummary.plannedDuration,
            completion = snapshot.dailySummary.completion,
        )
    }

    private fun WidgetTimeTask.mapToUi(
        language: String,
        secureMode: Boolean,
    ): WidgetTaskUi {
        val sourceTask = task
        return WidgetTaskUi(
            id = sourceTask.key,
            date = sourceTask.date.time,
            startTime = sourceTask.timeRange.from.time,
            endTime = sourceTask.timeRange.to.time,
            title = if (secureMode) {
                context.fetchWidgetString(language, R.string.widget_private_task)
            } else {
                sourceTask.category.fetchTitle(language)
            },
            subtitle = if (secureMode) null else sourceTask.subCategory?.name?.takeIf { it.isNotBlank() },
            categoryType = if (secureMode) null else sourceTask.category.default,
            priority = sourceTask.priority,
            status = status,
            isCompleted = sourceTask.isCompleted,
        )
    }

    private fun WidgetDeadlineTask.mapToUi(
        currentTime: Date,
        language: String,
        secureMode: Boolean,
    ): WidgetUndefinedTaskUi {
        val categoryTitle = task.mainCategory.fetchTitle(language)
        val subCategoryTitle = task.subCategory?.name?.takeIf { it.isNotBlank() }
        return WidgetUndefinedTaskUi(
            id = task.id,
            title = when {
                secureMode -> context.fetchWidgetString(language, R.string.widget_private_task)
                subCategoryTitle != null -> subCategoryTitle
                else -> categoryTitle
            },
            subtitle = when {
                secureMode -> null
                !task.note.isNullOrBlank() -> task.note
                subCategoryTitle != null -> categoryTitle
                else -> null
            },
            categoryType = if (secureMode) null else task.mainCategory.default,
            priority = task.priority,
            deadline = task.deadline?.time,
            deadlineTitle = fetchDeadlineTitle(task, type, currentTime, language),
            deadlineType = type,
        )
    }

    private fun WidgetWeekDay.mapToUi(
        currentTime: Date,
        locale: Locale,
    ): WidgetWeekDayUi {
        val dayDuration = date.shiftDay(1).time - date.time
        return WidgetWeekDayUi(
            date = date.time,
            dayTitle = SimpleDateFormat("EE", locale).format(date),
            dayNumber = SimpleDateFormat("d", locale).format(date),
            tasksCount = tasks.size,
            workload = workload,
            workloadProgress = if (dayDuration <= 0L) 0f else {
                (workload / dayDuration.toFloat()).coerceIn(0f, 1f)
            },
            progress = progress.coerceIn(0f, 1f),
            isToday = date.startThisDay() == currentTime.startThisDay(),
        )
    }

    private fun MainCategory.fetchTitle(language: String): String {
        val customTitle = customName
            ?.takeUnless { it == "null" }
            ?.takeIf { it.isNotBlank() }
        if (customTitle != null) return customTitle

        val strings = fetchCoreStrings(fetchCoreLanguage(language))
        return default?.mapToString(strings) ?: strings.categoryEmptyTitle
    }

    private fun fetchDeadlineTitle(
        task: UndefinedTask,
        type: WidgetDeadlineType,
        currentTime: Date,
        language: String,
    ): String {
        val deadline = task.deadline ?: return context.fetchWidgetString(
            language,
            R.string.widget_no_deadline,
        )
        val locale = fetchWidgetLocale(language)
        val timeTitle = DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(deadline)
        return when (type) {
            WidgetDeadlineType.OVERDUE -> {
                val overdueDays = max(
                    0L,
                    (currentTime.startThisDay().time - deadline.startThisDay().time) / MILLIS_IN_DAY,
                )
                if (overdueDays > 0L) {
                    context.fetchWidgetString(language, R.string.widget_overdue_days, overdueDays)
                } else {
                    context.fetchWidgetString(language, R.string.widget_overdue)
                }
            }
            WidgetDeadlineType.TODAY -> {
                context.fetchWidgetString(language, R.string.widget_today) + " · " + timeTitle
            }
            WidgetDeadlineType.UPCOMING -> {
                if (deadline.startThisDay() == currentTime.startThisDay().shiftDay(1)) {
                    context.fetchWidgetString(language, R.string.widget_tomorrow) + " · " + timeTitle
                } else {
                    SimpleDateFormat("d MMM", locale).format(deadline)
                }
            }
            WidgetDeadlineType.INBOX -> context.fetchWidgetString(language, R.string.widget_no_deadline)
        }
    }
}

private const val MILLIS_IN_DAY = 86_400_000L

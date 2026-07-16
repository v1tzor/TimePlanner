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
package ru.aleshin.timeplanner.core.ui.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.timeplanner.core.ui.R

/**
 * @author Stanislav Aleshin on 24.02.2023.
 */
data class TimePlannerIcons(
    val logo: Int,
    val logoCircular: Int,
    val splashIcon: Int,
    val categoryWorkIcon: Int,
    val categoryRestIcon: Int,
    val categorySportIcon: Int,
    val categoryCultureIcon: Int,
    val categorySleepIcon: Int,
    val categoryAffairsIcon: Int,
    val categoryTransportIcon: Int,
    val categoryStudyIcon: Int,
    val categoryEatIcon: Int,
    val categoryShopping: Int,
    val categoryHealth: Int,
    val categoryEntertainmentsIcon: Int,
    val categoryOtherIcon: Int,
    val arrowUp: Int,
    val arrowDown: Int,
    val categoryEmptyIcon: Int,
    val compactViewIcon: Int,
    val expandedViewIcon: Int,
    val schedulerIcon: Int,
    val categoriesIcon: Int,
    val templateTab: Int,
    val selectedTemplateTab: Int,
    val homeTab: Int,
    val selectedHomeTab: Int,
    val enabledSettingsIcon: Int,
    val disabledSettingsIcon: Int,
    val selectedAnalyticsTab: Int,
    val analyticsTab: Int,
    val categoryHygiene: Int,
    val time: Int,
    val reset: Int,
    val overviewTab: Int,
    val selectedOverviewTab: Int,
    val plannedTask: Int,
    val menuNavArrow: Int,
    val cancel: Int,
    val check: Int,
    val add: Int,
    val keyboard: Int,
)

internal val baseTimePlannerIcons = TimePlannerIcons(
    logo = R.drawable.ic_time_planner,
    logoCircular = R.drawable.ic_time_planner_circular,
    splashIcon = R.drawable.ic_splash,
    categoryWorkIcon = R.drawable.ic_work,
    categoryRestIcon = R.drawable.ic_rest,
    categorySportIcon = R.drawable.ic_sport,
    categoryCultureIcon = R.drawable.ic_culture,
    categorySleepIcon = R.drawable.ic_sleep,
    categoryAffairsIcon = R.drawable.ic_affairs,
    categoryTransportIcon = R.drawable.ic_car,
    categoryStudyIcon = R.drawable.ic_study,
    categoryEatIcon = R.drawable.ic_eat,
    categoryHealth = R.drawable.ic_medicine,
    categoryEntertainmentsIcon = R.drawable.ic_entertainments,
    categoryShopping = R.drawable.ic_store,
    categoryOtherIcon = R.drawable.ic_interests,
    arrowUp = R.drawable.ic_arrow_drop_up,
    arrowDown = R.drawable.ic_arrow_drop_down,
    categoryEmptyIcon = R.drawable.ic_close,
    compactViewIcon = R.drawable.ic_compact_view,
    expandedViewIcon = R.drawable.ic_expanded_view,
    schedulerIcon = R.drawable.ic_schedule,
    categoriesIcon = R.drawable.ic_categories,
    templateTab = R.drawable.ic_cards_star,
    selectedTemplateTab = R.drawable.ic_cards_star_fill,
    homeTab = R.drawable.ic_calendar_view_day,
    selectedHomeTab = R.drawable.ic_calendar_view_day_fill,
    enabledSettingsIcon = R.drawable.ic_settings,
    disabledSettingsIcon = R.drawable.ic_settings_outline,
    selectedAnalyticsTab = R.drawable.ic_analytics,
    analyticsTab = R.drawable.ic_analytics_outline,
    categoryHygiene = R.drawable.ic_face_retouching,
    time = R.drawable.ic_time,
    reset = R.drawable.ic_reset,
    overviewTab = R.drawable.ic_overview,
    selectedOverviewTab = R.drawable.ic_overview_fill,
    plannedTask = R.drawable.ic_planned_task,
    menuNavArrow = R.drawable.ic_arrow_right,
    cancel = R.drawable.ic_cancel,
    check = R.drawable.ic_check,
    add = R.drawable.ic_add,
    keyboard = R.drawable.ic_keyboard_outline,
)

val LocalTimePlannerIcons = staticCompositionLocalOf<TimePlannerIcons> {
    error("Core Icons is not provided")
}

fun fetchCoreIcons() = baseTimePlannerIcons

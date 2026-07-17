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
package ru.aleshin.features.templates.impl.presentation.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.features.templates.impl.R

/**
 * @author Stanislav Aleshin on 22.02.2023.
 */
internal data class TemplatesIcons(
    val notFound: Int,
    val category: Int,
    val subCategory: Int,
    val startTime: Int,
    val endTime: Int,
    val notification: Int,
    val info: Int,
    val priority: Int,
    val statistics: Int,
    val duration: Int,
    val time: Int,
    val repeat: Int,
    val updateRepeat: Int,
    val repeatVariant: Int,
    val pattern: Int,
    val stop: Int,
    val start: Int,
)

internal val baseTemplatesIcons = TemplatesIcons(
    notFound = R.drawable.ic_not_found,
    category = R.drawable.ic_category,
    subCategory = R.drawable.ic_subcategory,
    startTime = R.drawable.ic_start_time,
    endTime = R.drawable.ic_end_time,
    notification = R.drawable.ic_notification,
    info = R.drawable.ic_info,
    priority = R.drawable.ic_priority_high,
    statistics = R.drawable.ic_charts,
    duration = R.drawable.ic_timer,
    time = R.drawable.ic_time,
    repeat = R.drawable.ic_repeat,
    updateRepeat = R.drawable.ic_update_repeat,
    repeatVariant = R.drawable.ic_repeat_variant,
    pattern = R.drawable.ic_pattern,
    stop = R.drawable.ic_stop,
    start = R.drawable.ic_play,
)

internal val LocalTemplatesIcons = staticCompositionLocalOf<TemplatesIcons> {
    error("Templates icons are not provided")
}

internal fun fetchTemplatesIcons() = baseTemplatesIcons

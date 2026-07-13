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
package ru.aleshin.features.home.impl.presentation.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.features.home.impl.R

/**
 * @author Stanislav Aleshin on 22.02.2023.
 */
internal data class HomeIcons(
    val nextDate: Int,
    val previousDate: Int,
    val more: Int,
    val add: Int,
    val remove: Int,
    val calendar: Int,
    val check: Int,
    val notFound: Int,
    val cancel: Int,
    val notes: Int,
    val offNotifications: Int,
)

internal val baseHomeIcons = HomeIcons(
    nextDate = R.drawable.ic_next,
    previousDate = R.drawable.ic_previous,
    more = R.drawable.ic_more,
    add = R.drawable.ic_add,
    remove = R.drawable.ic_remove,
    calendar = R.drawable.ic_calendar,
    check = R.drawable.ic_check,
    notFound = R.drawable.ic_not_found,
    cancel = R.drawable.ic_cancel,
    notes = R.drawable.ic_notes,
    offNotifications = R.drawable.ic_bell_off,
)

internal val LocalHomeIcons = staticCompositionLocalOf<HomeIcons> {
    error("Home Icons is not provided")
}

internal fun fetchHomeIcons() = baseHomeIcons

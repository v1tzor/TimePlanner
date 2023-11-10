/*
 * Copyright 2023 Stanislav Aleshin
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
package ru.aleshin.features.editor.impl.presentation.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.editor.home.impl.R

/**
 * @author Stanislav Aleshin on 22.02.2023.
 */
internal data class EditorIcons(
    val add: Int,
    val more: Int,
    val showDialog: Int,
    val close: Int,
    val startTime: Int,
    val endTime: Int,
    val templates: Int,
    val delete: Int,
    val favorite: Int,
    val unFavorite: Int,
    val repeat: Int,
    val notes: Int,
    val deadline: Int,
    val subCategory: Int,
    val notesField: Int,
    val statistics: Int,
    val notifications: Int,
    val priority: Int,
)

internal val baseHomeIcons = EditorIcons(
    add = R.drawable.ic_add,
    more = R.drawable.ic_more,
    showDialog = R.drawable.ic_arrow_righ,
    close = R.drawable.ic_close,
    startTime = R.drawable.ic_start_time,
    endTime = R.drawable.ic_end_time,
    templates = R.drawable.ic_task,
    delete = R.drawable.ic_delete,
    favorite = R.drawable.ic_favorite,
    unFavorite = R.drawable.ic_favorite_border,
    repeat = R.drawable.ic_repeat,
    notes = R.drawable.ic_notes,
    deadline = R.drawable.ic_timer,
    subCategory = R.drawable.ic_sub_category,
    notesField = R.drawable.ic_note_outline,
    statistics = R.drawable.ic_chart_check,
    notifications = R.drawable.ic_notifications_outline,
    priority = R.drawable.ic_priority_high,
)

internal val LocalEditorIcons = staticCompositionLocalOf<EditorIcons> {
    error("Home Icons is not provided")
}

internal fun fetchEditorIcons() = baseHomeIcons

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
package ru.aleshin.timeplanner.presentation.widgets.main

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * @author Stanislav Aleshin on 28.04.2024.
 */
class MainWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = MainWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        MainWidgetUpdateWorker.enqueue(context)
    }

    companion object {
        val TASKS_KEY = stringPreferencesKey("daily_time_tasks")
        val DYNAMIC_COLOR = booleanPreferencesKey("is_dynamic_color_enabled")
        val COLORS_TYPE_KEY = stringPreferencesKey("colors_type")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val THEME_TYPE_KEY = stringPreferencesKey("theme_type")
    }
}

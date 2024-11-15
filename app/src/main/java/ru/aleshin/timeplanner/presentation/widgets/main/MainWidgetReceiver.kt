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
package ru.aleshin.timeplanner.presentation.widgets.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.Constants.Date.MILLIS_IN_MINUTE
import ru.aleshin.core.utils.functional.handleAndGet
import ru.aleshin.timeplanner.application.fetchApp
import ru.aleshin.timeplanner.di.component.AppComponent
import ru.aleshin.timeplanner.domain.entities.TimeTasks
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.04.2024.
 */
@OptIn(ExperimentalGlanceApi::class)
class MainWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = MainWidget()

    private val job = SupervisorJob()

    private val coroutineScope = CoroutineScope(coroutineContext + Dispatchers.IO + job)

    private var appComponent: AppComponent? = null

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if (appComponent == null) appComponent = context.applicationContext.fetchApp().appComponent
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (appComponent == null) appComponent = context.applicationContext.fetchApp().appComponent
        updateDataInfo(context)
        scheduleNextUpdate(context)
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        if (appComponent == null) appComponent = context?.applicationContext?.fetchApp()?.appComponent
        if (context != null) {
            updateDataInfo(context)
            scheduleNextUpdate(context)
        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        val cancelIntent = createBroadcastPendingIntent(context)
        if (cancelIntent != null) {
            context?.getSystemService<AlarmManager>()?.cancel(cancelIntent)
        }
    }

    private fun scheduleNextUpdate(context: Context) {
        val pendingIntent = createBroadcastPendingIntent(context) ?: return
        val calendar = (Calendar.getInstance().clone() as Calendar).apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, 1)
        }
        val alarmManager = context.getSystemService<AlarmManager>()
        alarmManager?.setInexactRepeating(AlarmManager.RTC, calendar.timeInMillis, MILLIS_IN_MINUTE, pendingIntent)
    }

    private fun createBroadcastPendingIntent(context: Context?): PendingIntent? {
        if (context == null) return null
        return PendingIntent.getBroadcast(context, 0, intent(context), FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
    }

    private fun updateDataInfo(context: Context) = coroutineScope.launch {
        val currentDate = Date().startThisDay()

        val timeTasksInteractor = appComponent?.fetchTimeTaskInteractor()
        val settingsInteractor = appComponent?.fetchSettingsInteractor()

        val timeTasks = timeTasksInteractor?.fetchTimeTasksByDate(currentDate)?.firstOrNull()?.handleAndGet(
            onLeftAction = { error(it) },
            onRightAction = { TimeTasks(it) },
        ) ?: return@launch

        val colorType = settingsInteractor?.fetchSettings()?.firstOrNull()?.handleAndGet(
            onLeftAction = { error(it) },
            onRightAction = { it.themeSettings.colorsType.toString() },
        ) ?: return@launch


        val widgetManager = GlanceAppWidgetManager(context)
        val glanceIdList = widgetManager.getGlanceIds(MainWidget::class.java)
        for (glanceId in glanceIdList) {
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[TASKS_KEY] = if (timeTasks.tasks.isNotEmpty()) Json.encodeToString(timeTasks) else ""
                    this[COLORS_TYPE_KEY] = colorType
                }
            }
            glanceAppWidget.update(context, glanceId)
        }
    }

    companion object {
        val TASKS_KEY = stringPreferencesKey("daily_time_tasks")

        val COLORS_TYPE_KEY = stringPreferencesKey("colors_type")

        fun intent(context: Context) = Intent(context, MainWidgetReceiver::class.java).apply {
            action = "${context.packageName}.tick"
        }
    }
}

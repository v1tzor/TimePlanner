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

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import ru.aleshin.core.utils.extensions.fetchLocale
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.firstRightOrNull
import ru.aleshin.timeplanner.application.fetchApp
import ru.aleshin.timeplanner.domain.entities.TimeTasks
import ru.aleshin.timeplanner.presentation.widgets.main.MainWidgetReceiver.Companion.COLORS_TYPE_KEY
import ru.aleshin.timeplanner.presentation.widgets.main.MainWidgetReceiver.Companion.DYNAMIC_COLOR
import ru.aleshin.timeplanner.presentation.widgets.main.MainWidgetReceiver.Companion.LANGUAGE_KEY
import ru.aleshin.timeplanner.presentation.widgets.main.MainWidgetReceiver.Companion.TASKS_KEY
import ru.aleshin.timeplanner.presentation.widgets.main.MainWidgetReceiver.Companion.THEME_TYPE_KEY
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class MainWidgetUpdateWorker(
    context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val widget = MainWidget()
            val glanceIds = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(MainWidget::class.java)

            if (glanceIds.isEmpty()) return Result.success()

            val appComponent = applicationContext.fetchApp().appComponent
            val currentDate = Date().startThisDay()
            val tasks = appComponent.fetchTimeTaskInteractor()
                .fetchTimeTasksByDate(currentDate)
                .firstRightOrNull()
                ?: return Result.failure()
            val themeSettings = appComponent.fetchSettingsInteractor()
                .fetchSettings()
                .firstRightOrNull()
                ?.themeSettings
                ?: return Result.failure()
            val timeTasks = TimeTasks(tasks)

            glanceIds.forEach { glanceId ->
                updateAppWidgetState(
                    context = applicationContext,
                    definition = PreferencesGlanceStateDefinition,
                    glanceId = glanceId,
                ) { preferences ->
                    preferences.toMutablePreferences().apply {
                        this[TASKS_KEY] = if (tasks.isEmpty()) "" else Json.encodeToString(timeTasks)
                        this[DYNAMIC_COLOR] = themeSettings.isDynamicColorEnable
                        this[COLORS_TYPE_KEY] = themeSettings.colorsType.name
                        this[LANGUAGE_KEY] = themeSettings.language.code ?: applicationContext.fetchLocale().language
                        this[THEME_TYPE_KEY] = themeSettings.themeColors.name
                    }
                }
                widget.update(applicationContext, glanceId)
            }
            Result.success()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.failure()
        }
    }

    companion object {

        private const val WORK_NAME = "MAIN_WIDGET_UPDATE"

        fun enqueue(context: Context) {
            WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<MainWidgetUpdateWorker>().build(),
            )
        }
    }
}

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
package ru.aleshin.timeplanner.widgets.presentation.work

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellationException
import ru.aleshin.core.utils.functional.rightOrNull
import ru.aleshin.timeplanner.widgets.domain.interactors.WidgetsInteractor
import ru.aleshin.timeplanner.widgets.presentation.mappers.WidgetsStateUiMapper
import ru.aleshin.timeplanner.widgets.presentation.models.WidgetThemeUi
import ru.aleshin.timeplanner.widgets.presentation.state.WidgetStateCodec
import ru.aleshin.timeplanner.widgets.presentation.state.WidgetStateKeys
import ru.aleshin.timeplanner.widgets.presentation.ui.deadlines.DeadlinesWidget
import ru.aleshin.timeplanner.widgets.presentation.ui.summary.DailySummaryWidget
import ru.aleshin.timeplanner.widgets.presentation.ui.today.TodayWidget
import ru.aleshin.timeplanner.widgets.presentation.ui.week.WeekOverviewWidget

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class WidgetsUpdateWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val widgetsInteractor: WidgetsInteractor,
    private val stateMapper: WidgetsStateUiMapper,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val targets = fetchInstalledTargets()

            if (targets.isEmpty()) {
                WidgetsUpdateScheduler.cancelBoundary(applicationContext)
                return Result.success()
            }

            val snapshot = widgetsInteractor.fetchSnapshot().rightOrNull() ?: return Result.retry()
            val theme = stateMapper.mapTheme(snapshot)

            targets.forEach { target ->
                val payload = when (target.widget) {
                    is TodayWidget -> WidgetStateCodec.encode(stateMapper.mapToday(snapshot))
                    is DeadlinesWidget -> WidgetStateCodec.encode(stateMapper.mapDeadlines(snapshot))
                    is WeekOverviewWidget -> WidgetStateCodec.encode(stateMapper.mapWeek(snapshot))
                    is DailySummaryWidget -> WidgetStateCodec.encode(stateMapper.mapSummary(snapshot))
                    else -> return@forEach
                }
                updateTarget(target, payload, theme)
            }
            WidgetsUpdateScheduler.scheduleBoundary(applicationContext, snapshot.nextUpdateAt)
            Result.success()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            if (runAttemptCount < MAX_RETRY_COUNT) Result.retry() else Result.failure()
        }
    }

    private suspend fun fetchInstalledTargets(): List<WidgetTarget> {
        val manager = GlanceAppWidgetManager(applicationContext)

        return buildList {
            addTargets(manager, TodayWidget())
            addTargets(manager, DeadlinesWidget())
            addTargets(manager, WeekOverviewWidget())
            addTargets(manager, DailySummaryWidget())
        }
    }

    private suspend fun MutableList<WidgetTarget>.addTargets(
        manager: GlanceAppWidgetManager,
        widget: GlanceAppWidget,
    ) {
        manager.getGlanceIds(widget.javaClass).forEach { id ->
            add(WidgetTarget(widget, id))
        }
    }

    private suspend fun updateTarget(
        target: WidgetTarget,
        payload: String,
        theme: WidgetThemeUi,
    ) {
        updateAppWidgetState(
            context = applicationContext,
            definition = PreferencesGlanceStateDefinition,
            glanceId = target.id,
        ) { preferences ->
            preferences.toMutablePreferences().apply {
                setupState(payload, theme)
            }
        }
        target.widget.update(applicationContext, target.id)
    }

    private fun MutablePreferences.setupState(
        payload: String,
        theme: WidgetThemeUi,
    ) {
        this[WidgetStateKeys.payload] = payload
        this[WidgetStateKeys.language] = theme.language.code
        this[WidgetStateKeys.theme] = theme.theme.name
    }

    private data class WidgetTarget(
        val widget: GlanceAppWidget,
        val id: GlanceId,
    )

    companion object {
        private const val MAX_RETRY_COUNT = 3
    }
}

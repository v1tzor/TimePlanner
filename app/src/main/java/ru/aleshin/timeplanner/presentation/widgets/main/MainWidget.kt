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

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.compose.runtime.remember
import androidx.glance.GlanceId
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import kotlinx.serialization.json.Json
import ru.aleshin.core.utils.functional.Constants.App.EDITOR_DEEP_LINK
import ru.aleshin.timeplanner.domain.entities.TimeTasks
import ru.aleshin.timeplanner.presentation.ui.main.MainActivity
import ru.aleshin.timeplanner.presentation.widgets.WidgetTheme
import ru.aleshin.timeplanner.presentation.widgets.main.MainWidgetReceiver.Companion.TASKS_KEY
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.04.2024.
 */
class MainWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override val stateDefinition: GlanceStateDefinition<*>
        get() = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) = provideContent {
        WidgetTheme(context) {
            val currentTime = Date()
            val stateTimeTasks = currentState(TASKS_KEY) ?: ""
            val timeTasks = remember(stateTimeTasks) {
                if (stateTimeTasks.isNotEmpty()) {
                    Json.decodeFromString<TimeTasks>(stateTimeTasks)
                } else {
                    TimeTasks()
                }
            }
            MainWidgetContent(
                currentTime = currentTime,
                timeTasks = timeTasks.tasks,
                onTimeTaskClickAction = { actionStartActivity<MainActivity>() },
                onUpdateClickAction = { actionSendBroadcast<MainWidgetReceiver>() },
                onAddAction = {
                    val mainActivityUri = Uri.parse(EDITOR_DEEP_LINK)
                    actionStartActivity(Intent(ACTION_VIEW, mainActivityUri))
                },
            )
        }
    }
}
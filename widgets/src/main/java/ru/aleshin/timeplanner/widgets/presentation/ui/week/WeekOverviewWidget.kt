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
package ru.aleshin.timeplanner.widgets.presentation.ui.week

import android.content.Context
import androidx.compose.runtime.remember
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import ru.aleshin.timeplanner.widgets.presentation.ui.week.state.WeekOverviewWidgetStateUi
import ru.aleshin.timeplanner.widgets.presentation.state.WidgetStateCodec
import ru.aleshin.timeplanner.widgets.presentation.state.WidgetStateKeys
import ru.aleshin.timeplanner.widgets.presentation.theme.WidgetTheme

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class WeekOverviewWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override val stateDefinition: GlanceStateDefinition<*>
        get() = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) = provideContent {
        WidgetTheme(context) {
            val payload = currentState(WidgetStateKeys.payload)
            val state = remember(payload) {
                WidgetStateCodec.decodeCurrentOrDefault(
                    value = payload,
                    version = WeekOverviewWidgetStateUi::version,
                    defaultValue = ::WeekOverviewWidgetStateUi,
                )
            }
            WeekOverviewWidgetContent(state)
        }
    }
}

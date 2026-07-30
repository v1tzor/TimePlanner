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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsState
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.layouts.AnalyticsBookLayout
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.layouts.AnalyticsGridLayout
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.layouts.AnalyticsSinglePaneLayout
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
internal fun AnalyticsLayout(
    modifier: Modifier = Modifier,
    state: AnalyticsState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onOpenCalendar: () -> Unit,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    when {
        adaptiveLayoutInfo.isBookPosture -> AnalyticsBookLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onOpenCalendar = onOpenCalendar,
            onEvent = onEvent,
        )
        adaptiveLayoutInfo.isCompactWidth -> AnalyticsSinglePaneLayout(
            modifier = modifier,
            state = state,
            onOpenCalendar = onOpenCalendar,
            onEvent = onEvent,
        )
        else -> AnalyticsGridLayout(
            modifier = modifier,
            state = state,
            isExpanded = adaptiveLayoutInfo.useExpandedLayout,
            onOpenCalendar = onOpenCalendar,
            onEvent = onEvent,
        )
    }
}

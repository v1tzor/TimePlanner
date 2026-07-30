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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsEmptyCard
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsErrorCard

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun AnalyticsErrorState(
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit,
) {
    AnalyticsErrorCard(
        modifier = modifier,
        text = AnalyticsThemeRes.strings.error,
        retryTitle = AnalyticsThemeRes.strings.retry,
        onRetry = onRetryClick,
    )
}

@Composable
internal fun AnalyticsEmptyState(
    modifier: Modifier = Modifier,
) {
    AnalyticsEmptyCard(
        modifier = modifier,
        text = AnalyticsThemeRes.strings.noData,
        supportingText = AnalyticsThemeRes.strings.noDataSupporting,
    )
}

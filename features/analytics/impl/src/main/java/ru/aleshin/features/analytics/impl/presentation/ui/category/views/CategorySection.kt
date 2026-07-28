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
package ru.aleshin.features.analytics.impl.presentation.ui.category.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsSectionTitle
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsSurfaceCard

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
@Composable
internal fun CategorySection(
    modifier: Modifier = Modifier,
    title: String,
    fillAvailableHeight: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalSpacing: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AnalyticsSectionTitle(title = title)
        AnalyticsSurfaceCard(
            modifier = if (fillAvailableHeight) Modifier.weight(1f) else Modifier,
            contentPadding = contentPadding,
            verticalSpacing = verticalSpacing,
            content = content,
        )
    }
}

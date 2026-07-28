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
package ru.aleshin.features.templates.impl.presentation.theme.tokens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal object TemplatesLayoutDefaults {

    val CompactContentPadding = PaddingValues(
        start = 16.dp,
        top = 12.dp,
        end = 16.dp,
        bottom = 88.dp,
    )
    val MainPaneContentPadding = PaddingValues(
        start = 16.dp,
        top = 12.dp,
        end = 16.dp,
        bottom = 88.dp,
    )

    val CompactGridHorizontalSpacing = 10.dp
    val GridVerticalSpacing = 12.dp
    val AdaptiveGridSpacing = 16.dp
    val TemplateCardMinWidth = 228.dp

    val MainPaneMinWidth = 320.dp
    val SupportingPaneMinWidth = 320.dp
    val SupportingPanePreferredWidth = 384.dp
    val SupportingPaneMaxWidth = 480.dp

    val SupportingPanePadding = 16.dp
    val SupportingPaneSectionSpacing = 16.dp
    val PatternHeaderIconContainerSize = 40.dp
    val PatternHeaderIconSize = 22.dp
    val PatternFilterPlaceholderHeight = 40.dp
    val CalendarPlaceholderHeight = 392.dp
    val CalendarContentPadding = 16.dp
    val CalendarDaySpacing = 4.dp
    val CalendarDayContentPadding = 5.dp
    val CalendarMarkerSize = 5.dp
    val CalendarMarkerSpacing = 2.dp
    val CurrentDayBorderWidth = 1.dp
}

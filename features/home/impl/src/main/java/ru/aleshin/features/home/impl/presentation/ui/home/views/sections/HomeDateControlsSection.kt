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
package ru.aleshin.features.home.impl.presentation.ui.home.views.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.settings.ViewToggleStatus
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeDateChooser
import ru.aleshin.timeplanner.core.ui.theme.material.topSide
import ru.aleshin.timeplanner.core.ui.views.ViewToggle
import java.util.Date

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun HomeDateControlsSection(
    modifier: Modifier = Modifier,
    selectedDate: Date?,
    toggleState: ViewToggleStatus,
    isToggleVisible: Boolean,
    onDateChange: (Date) -> Unit,
    onOpenCalendar: () -> Unit,
    onViewToggleChange: (ViewToggleStatus) -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge.topSide,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 12.dp,
                end = 8.dp,
                bottom = 12.dp,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            HomeDateChooser(
                modifier = Modifier.width(202.dp),
                selectedDate = selectedDate,
                onDateChange = onDateChange,
                onOpenCalendar = onOpenCalendar,
            )
            Spacer(modifier = Modifier.weight(1f))
            AnimatedVisibility(
                visible = isToggleVisible,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut(),
            ) {
                ViewToggle(
                    status = toggleState,
                    onStatusChange = onViewToggleChange,
                )
            }
        }
    }
}

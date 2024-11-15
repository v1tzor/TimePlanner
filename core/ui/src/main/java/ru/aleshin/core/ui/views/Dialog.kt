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
package ru.aleshin.core.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
fun DialogButtons(
    modifier: Modifier = Modifier,
    enabledConfirm: Boolean = true,
    confirmTitle: String = TimePlannerRes.strings.confirmTitle,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    Row(
        modifier = modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp, start = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onCancelClick) {
            Text(
                text = TimePlannerRes.strings.cancelTitle,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        TextButton(enabled = enabledConfirm, onClick = onConfirmClick) {
            Text(
                text = confirmTitle,
                color = when (enabledConfirm) {
                    true -> MaterialTheme.colorScheme.primary
                    false -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
fun DialogButtons(
    modifier: Modifier = Modifier,
    confirmFirstTitle: String,
    confirmSecondTitle: String,
    onCancelClick: () -> Unit,
    onConfirmFirstClick: () -> Unit,
    onConfirmSecondClick: () -> Unit,
) {
    Row(
        modifier = modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp, start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextButton(onClick = onCancelClick) {
            Text(
                text = TimePlannerRes.strings.cancelTitle,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onConfirmFirstClick) {
            Text(
                text = confirmFirstTitle,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        TextButton(onClick = onConfirmSecondClick) {
            Text(
                text = confirmSecondTitle,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
fun DialogHeader(
    modifier: Modifier = Modifier,
    header: String,
    title: String? = null,
    paddingValues: PaddingValues = PaddingValues(top = 24.dp, bottom = 12.dp, start = 24.dp, end = 24.dp),
    headerColor: Color = MaterialTheme.colorScheme.onSurface,
    titleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Column(modifier = modifier.padding(paddingValues)) {
        Text(
            text = header,
            color = headerColor,
            style = MaterialTheme.typography.headlineSmall,
        )
        if (title != null) {
            Text(
                text = title,
                color = titleColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
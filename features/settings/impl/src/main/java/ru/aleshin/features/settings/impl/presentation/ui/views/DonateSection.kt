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
package ru.aleshin.features.settings.impl.presentation.ui.views

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.material.surfaceTwo
import ru.aleshin.core.ui.views.ExpandedIcon
import ru.aleshin.features.settings.impl.presentation.models.CryptoAddress
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes

/**
 * @author Stanislav Aleshin on 14.09.2023.
 */ 
@Composable
internal fun DonateView(
    modifier: Modifier = Modifier,
    onDonate: () -> Unit,
    onCopyCryptoAddress: (String) -> Unit,
) {
    var openCryptoList by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.animateContentSize(), 
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(modifier = Modifier.weight(1f), onClick = onDonate) {
                Text(text = SettingsThemeRes.strings.donateTitle)
            }
            Surface(
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primary,
                onClick = { openCryptoList = !openCryptoList },
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ExpandedIcon(isExpanded = openCryptoList, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
        if (openCryptoList) {
            CryptoList(onCopyCryptoAddress = onCopyCryptoAddress)
        }
    }
}

@Composable
internal fun CryptoList(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: LazyListState = rememberLazyListState(),
    onCopyCryptoAddress: (String) -> Unit,
) {
    Surface(
        modifier = modifier.height(310.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceTwo(),
    ) {
        LazyColumn(
            state = state,
            userScrollEnabled = enabled,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        ) {
            items(CryptoAddress.values()) { address ->
                CryptoItem(
                    name = address.crypto,
                    onCopy = { onCopyCryptoAddress(address.address) },
                )
            }
        }
    }
}

@Composable
internal fun CryptoItem(
    modifier: Modifier = Modifier,
    name: String,
    onCopy: () -> Unit,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onCopy) {
            Text(
                text = SettingsThemeRes.strings.copyTitle, 
                color = MaterialTheme.colorScheme.primary,
            )
        }
    } 
}

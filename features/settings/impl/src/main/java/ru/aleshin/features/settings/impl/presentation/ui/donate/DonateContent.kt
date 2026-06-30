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
package ru.aleshin.features.settings.impl.presentation.ui.donate

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.core.utils.extensions.setClipboard
import ru.aleshin.features.settings.impl.presentation.mappers.mapToIcon
import ru.aleshin.features.settings.impl.presentation.models.CryptoAddress
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateEvent
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateState
import ru.aleshin.features.settings.impl.presentation.ui.donate.store.DonateComponent
import ru.aleshin.features.settings.impl.presentation.ui.donate.views.DonateTopAppBar

/**
 * @author Stanislav Aleshin on 13.10.2023.
 */
@Composable
internal fun DonateContent(
    donateComponent: DonateComponent,
    modifier: Modifier = Modifier,
) {
    val store = donateComponent.store
    val state by store.stateAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        content = { paddingValues ->
            BaseDonateContent(
                state = state,
                modifier = Modifier.padding(paddingValues),
            )
        },
        topBar = {
            DonateTopAppBar(
                onNavButtonClick = { store.dispatchEvent(DonateEvent.PressBackButton) },
            )
        },
    )
}

@Composable
private fun BaseDonateContent(
    state: DonateState,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        items(CryptoAddress.entries.toTypedArray()) { address ->
            Text(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                text = address.cryptoName,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                style = MaterialTheme.typography.titleSmall,
            )
            CryptoAddressItem(
                model = address,
                onCopy = { setClipboard(context, it) },
            )
        }
        item {
            Spacer(modifier = Modifier.padding(48.dp))
        }
    }
}

@Composable
internal fun CryptoAddressItem(
    modifier: Modifier = Modifier,
    model: CryptoAddress,
    onCopy: (String) -> Unit,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium,
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = model.mapToIcon()),
                        contentDescription = model.cryptoName,
                    )
                }
            }
            Text(
                modifier = Modifier.weight(1f),
                text = model.address,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = { onCopy(model.address) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(id = SettingsThemeRes.icons.copy),
                    contentDescription = SettingsThemeRes.strings.copyTitle,
                )
            }
        }
    }
}

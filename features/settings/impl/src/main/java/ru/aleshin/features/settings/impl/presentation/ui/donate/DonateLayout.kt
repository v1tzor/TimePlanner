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
package ru.aleshin.features.settings.impl.presentation.ui.donate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.extensions.setClipboard
import ru.aleshin.features.settings.impl.presentation.models.CryptoAddress
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateState
import ru.aleshin.features.settings.impl.presentation.ui.donate.views.CryptoAddressCard
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
@Composable
internal fun DonateLayout(
    state: DonateState,
    modifier: Modifier = Modifier,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
) {
    if (adaptiveLayoutInfo.useExpandedLayout) {
        DonateExpandedLayout(
            modifier = modifier,
            isLargeWidth = adaptiveLayoutInfo.isLargeWidth || adaptiveLayoutInfo.isExtraLargeWidth
        )
    } else {
        DonateCompactLayout(
            modifier = modifier,
            isMediumWidth = adaptiveLayoutInfo.isMediumWidth
        )
    }
}

@Composable
private fun DonateCompactLayout(
    modifier: Modifier = Modifier,
    isMediumWidth: Boolean
) {
    val listState = rememberLazyListState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        val context = LocalContext.current

        LazyColumn(
            modifier = Modifier
                .then(
                    other = if (isMediumWidth) {
                        Modifier.widthIn(max = AdaptiveLayoutDefaults.SettingsContentMaxWidth)
                    } else {
                        Modifier
                    }
                )
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            items(CryptoAddress.entries.toTypedArray()) { address ->
                CryptoAddressCard(
                    address = address,
                    onCopy = { setClipboard(context, it) },
                )
            }
            item {
                Spacer(modifier = Modifier.padding(48.dp))
            }
        }
    }
}

@Composable
private fun DonateExpandedLayout(
    modifier: Modifier = Modifier,
    isLargeWidth: Boolean
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        val context = LocalContext.current

        LazyVerticalGrid(
            columns = GridCells.Fixed(
                count = if (isLargeWidth) LARGE_COLUMN_COUNT else EXPANDED_COLUMN_COUNT,
            ),
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.ExpandedContentMaxWidth)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = AdaptiveLayoutDefaults.ExpandedHorizontalPadding,
                vertical = 8.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(AdaptiveLayoutDefaults.GridSpacing),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            items(CryptoAddress.entries.toTypedArray()) { address ->
                CryptoAddressCard(
                    address = address,
                    onCopy = { setClipboard(context, it) },
                )
            }
            item {
                Spacer(modifier = Modifier.padding(48.dp))
            }
        }
    }
}

private const val EXPANDED_COLUMN_COUNT = 2
private const val LARGE_COLUMN_COUNT = 3
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

package ru.aleshin.timeplanner.core.ui.views

import androidx.compose.foundation.focusGroup
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.HingePolicy
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculateThreePaneScaffoldValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun AdaptiveListDetailPaneScaffold(
    modifier: Modifier = Modifier,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    listPanePreferredWidth: Dp = AdaptiveLayoutDefaults.ListPanePreferredWidth,
    listPane: @Composable () -> Unit,
    detailPane: @Composable () -> Unit,
) {
    val adaptiveInfo = remember(
        adaptiveLayoutInfo.windowSizeClass,
        adaptiveLayoutInfo.posture,
    ) {
        WindowAdaptiveInfo(
            windowSizeClass = adaptiveLayoutInfo.windowSizeClass,
            windowPosture = adaptiveLayoutInfo.posture,
        )
    }
    val directive = calculatePaneScaffoldDirective(
        windowAdaptiveInfo = adaptiveInfo,
        verticalHingePolicy = HingePolicy.AvoidSeparating,
    )
    val adaptStrategies = remember {
        ListDetailPaneScaffoldDefaults.adaptStrategies()
    }
    val scaffoldValue = calculateThreePaneScaffoldValue(
        maxHorizontalPartitions = directive.maxHorizontalPartitions,
        maxVerticalPartitions = directive.maxVerticalPartitions,
        adaptStrategies = adaptStrategies,
        currentDestination = null,
    )

    ListDetailPaneScaffold(
        directive = directive,
        value = scaffoldValue,
        modifier = modifier,
        listPane = {
            AnimatedPane(
                modifier = Modifier
                    .preferredWidth(listPanePreferredWidth)
                    .focusGroup(),
            ) {
                listPane()
            }
        },
        detailPane = {
            AnimatedPane(modifier = Modifier.focusGroup()) {
                detailPane()
            }
        },
    )
}

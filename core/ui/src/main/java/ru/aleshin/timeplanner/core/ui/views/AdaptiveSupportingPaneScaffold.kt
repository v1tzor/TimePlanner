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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AdaptStrategy
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.HingePolicy
import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldValue
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth
import androidx.compose.material3.adaptive.layout.calculateThreePaneScaffoldValue
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.isSpecified
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun AdaptiveSupportingPaneScaffold(
    modifier: Modifier = Modifier,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    mainPaneMinWidth: Dp = Dp.Unspecified,
    mainPaneMaxWidth: Dp = Dp.Unspecified,
    supportingPaneMinWidth: Dp = Dp.Unspecified,
    supportingPaneMaxWidth: Dp = Dp.Unspecified,
    supportingPanePreferredWidth: Dp = AdaptiveLayoutDefaults.SupportingPanePreferredWidth,
    useTwoPanesOnMediumWidth: Boolean = false,
    showPaneExpansionDragHandle: Boolean = false,
    mainPane: @Composable () -> Unit,
    supportingPane: @Composable () -> Unit,
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
    val directive = if (useTwoPanesOnMediumWidth) {
        calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(
            windowAdaptiveInfo = adaptiveInfo,
            verticalHingePolicy = HingePolicy.AvoidSeparating,
        )
    } else {
        calculatePaneScaffoldDirective(
            windowAdaptiveInfo = adaptiveInfo,
            verticalHingePolicy = HingePolicy.AvoidSeparating,
        )
    }
    val adaptStrategies = remember {
        SupportingPaneScaffoldDefaults.adaptStrategies(
            supportingPaneAdaptStrategy = AdaptStrategy.Reflow(
                reflowUnder = SupportingPaneScaffoldRole.Main,
            ),
        )
    }
    val scaffoldValue = calculateThreePaneScaffoldValue(
        maxHorizontalPartitions = directive.maxHorizontalPartitions,
        maxVerticalPartitions = directive.maxVerticalPartitions,
        adaptStrategies = adaptStrategies,
        currentDestination = null,
    )
    val paneExpansionConfiguration = rememberPaneExpansionConfiguration(
        isEnabled = showPaneExpansionDragHandle,
        mainPaneMinWidth = mainPaneMinWidth,
        mainPaneMaxWidth = mainPaneMaxWidth,
        supportingPaneMinWidth = supportingPaneMinWidth,
        supportingPaneMaxWidth = supportingPaneMaxWidth,
        paneSpacerWidth = directive.horizontalPartitionSpacerSize,
        scaffoldValue = scaffoldValue,
    )

    SupportingPaneScaffold(
        directive = directive,
        value = scaffoldValue,
        modifier = modifier,
        mainPane = {
            AnimatedPane(modifier = Modifier.focusGroup()) {
                mainPane()
            }
        },
        supportingPane = {
            AnimatedPane(
                modifier = Modifier
                    .preferredWidth(supportingPanePreferredWidth)
                    .focusGroup(),
            ) {
                supportingPane()
            }
        },
        paneExpansionState = paneExpansionConfiguration?.state,
        paneExpansionDragHandle = if (showPaneExpansionDragHandle) {
            { state ->
                AdaptivePaneExpansionDragHandle(
                    state = state,
                    dragLimiter = paneExpansionConfiguration?.dragLimiter,
                )
            }
        } else {
            null
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun rememberPaneExpansionConfiguration(
    isEnabled: Boolean,
    mainPaneMinWidth: Dp,
    mainPaneMaxWidth: Dp,
    supportingPaneMinWidth: Dp,
    supportingPaneMaxWidth: Dp,
    paneSpacerWidth: Dp,
    scaffoldValue: ThreePaneScaffoldValue,
): PaneExpansionConfiguration? {
    if (!isEnabled || !mainPaneMinWidth.isSpecified || !supportingPaneMinWidth.isSpecified) {
        return null
    }

    val layoutDirection = LocalLayoutDirection.current
    val paneSpacerHalfWidth = paneSpacerWidth / 2
    val mainPaneDividerInset = mainPaneMinWidth + paneSpacerHalfWidth
    val supportingPaneDividerInset = supportingPaneMinWidth + paneSpacerHalfWidth
    val mainPaneDividerMaxInset = when {
        mainPaneMaxWidth.isSpecified -> mainPaneMaxWidth + paneSpacerHalfWidth
        else -> Dp.Unspecified
    }
    val supportingPaneDividerMaxInset = when {
        supportingPaneMaxWidth.isSpecified -> supportingPaneMaxWidth + paneSpacerHalfWidth
        else -> Dp.Unspecified
    }
    val density = LocalDensity.current
    val dragLimiter = remember(
        mainPaneDividerInset,
        mainPaneDividerMaxInset,
        supportingPaneDividerInset,
        supportingPaneDividerMaxInset,
        density,
        layoutDirection,
    ) {
        val mainPaneDividerInsetPx = with(density) { mainPaneDividerInset.toPx() }
        val supportingPaneDividerInsetPx = with(density) {
            supportingPaneDividerInset.toPx()
        }
        val mainPaneDividerMaxInsetPx = when {
            mainPaneDividerMaxInset.isSpecified -> with(density) {
                mainPaneDividerMaxInset.toPx()
            }
            else -> Float.POSITIVE_INFINITY
        }
        val supportingPaneDividerMaxInsetPx = when {
            supportingPaneDividerMaxInset.isSpecified -> with(density) {
                supportingPaneDividerMaxInset.toPx()
            }
            else -> Float.POSITIVE_INFINITY
        }
        PaneExpansionDragLimiter(
            firstPaneMinWidth = when (layoutDirection) {
                LayoutDirection.Ltr -> mainPaneDividerInsetPx
                LayoutDirection.Rtl -> supportingPaneDividerInsetPx
            },
            secondPaneMinWidth = when (layoutDirection) {
                LayoutDirection.Ltr -> supportingPaneDividerInsetPx
                LayoutDirection.Rtl -> mainPaneDividerInsetPx
            },
            firstPaneMaxWidth = when (layoutDirection) {
                LayoutDirection.Ltr -> mainPaneDividerMaxInsetPx
                LayoutDirection.Rtl -> supportingPaneDividerMaxInsetPx
            },
            secondPaneMaxWidth = when (layoutDirection) {
                LayoutDirection.Ltr -> supportingPaneDividerMaxInsetPx
                LayoutDirection.Rtl -> mainPaneDividerMaxInsetPx
            },
        )
    }
    val state = rememberPaneExpansionState(
        keyProvider = scaffoldValue,
        consumeDragDelta = dragLimiter::consumeDragDelta,
    )
    return remember(state, dragLimiter) {
        PaneExpansionConfiguration(
            state = state,
            dragLimiter = dragLimiter,
        )
    }
}

/**
 * @author Stanislav Aleshin on 27.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun ThreePaneScaffoldScope.AdaptivePaneExpansionDragHandle(
    modifier: Modifier = Modifier,
    state: PaneExpansionState,
    dragLimiter: PaneExpansionDragLimiter?,
) {
    val interactionSource = remember { MutableInteractionSource() }

    VerticalDragHandle(
        modifier = modifier
            .paneExpansionDraggable(
                state = state,
                minTouchTargetSize = LocalMinimumInteractiveComponentSize.current,
                interactionSource = interactionSource,
            )
            .onGloballyPositioned { coordinates ->
                val scaffoldWidth = coordinates.parentLayoutCoordinates?.size?.width ?: return@onGloballyPositioned
                val dividerOffset = coordinates.positionInParent().x + coordinates.size.width / 2f
                dragLimiter?.updateLayout(
                    dividerOffset = dividerOffset,
                    scaffoldWidth = scaffoldWidth.toFloat(),
                )
            },
        interactionSource = interactionSource,
    )
}

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

package ru.aleshin.timeplanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.Posture
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.android.tools.screenshot.PreviewTest
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold

@PreviewTest
@Preview(name = "Compact portrait", widthDp = 412, heightDp = 915, showBackground = true)
@Composable
fun CompactPortraitAdaptiveLayoutScreenshot() {
    AdaptiveLayoutFixture(width = 412, height = 915)
}

@PreviewTest
@Preview(name = "Short wide phone", widthDp = 840, heightDp = 460, showBackground = true)
@Composable
fun ShortWideAdaptiveLayoutScreenshot() {
    AdaptiveLayoutFixture(width = 840, height = 460)
}

@PreviewTest
@Preview(name = "Medium portrait", widthDp = 700, heightDp = 1000, showBackground = true)
@Composable
fun MediumPortraitAdaptiveLayoutScreenshot() {
    AdaptiveLayoutFixture(width = 700, height = 1000)
}

@PreviewTest
@Preview(name = "Expanded landscape", widthDp = 1280, heightDp = 800, showBackground = true)
@Composable
fun ExpandedLandscapeAdaptiveLayoutScreenshot() {
    AdaptiveLayoutFixture(width = 1280, height = 800)
}

@PreviewTest
@Preview(name = "Large desktop", widthDp = 1600, heightDp = 900, showBackground = true)
@Composable
fun LargeDesktopAdaptiveLayoutScreenshot() {
    AdaptiveLayoutFixture(width = 1600, height = 900)
}

@Composable
private fun AdaptiveLayoutFixture(
    width: Int,
    height: Int,
) {
    val adaptiveLayoutInfo = AdaptiveLayoutInfo(
        windowSizeClass = WindowSizeClass(width, height),
        windowSize = DpSize(width.dp, height.dp),
        posture = Posture(),
    )
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (adaptiveLayoutInfo.useExpandedLayout) {
                AdaptiveSupportingPaneScaffold(
                    modifier = Modifier.fillMaxSize(),
                    adaptiveLayoutInfo = adaptiveLayoutInfo,
                    mainPane = {
                        AdaptiveFixtureCards(columns = 2)
                    },
                    supportingPane = {
                        AdaptiveFixturePane()
                    },
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    AdaptiveFixtureCards(
                        modifier = Modifier.fillMaxWidth(),
                        columns = if (adaptiveLayoutInfo.isCompactWidth) 1 else 2,
                    )
                }
            }
        }
    }
}

@Composable
private fun AdaptiveFixtureCards(
    modifier: Modifier = Modifier,
    columns: Int,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items((1..8).toList()) { index ->
            Card {
                Text(
                    modifier = Modifier.padding(24.dp),
                    text = "Adaptive card $index",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun AdaptiveFixturePane() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Supporting pane",
            style = MaterialTheme.typography.headlineSmall,
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(24.dp),
                text = "Fold-aware controls",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

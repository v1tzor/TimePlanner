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
 * imitations under the License.
 */
package ru.aleshin.core.ui.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlinx.coroutines.launch
import ru.aleshin.core.ui.theme.material.surfaceTwo
import kotlin.math.absoluteValue

@Composable
@ExperimentalFoundationApi
fun <T : TabItem> HorizontalTabsPager(
    modifier: Modifier = Modifier,
    state: PagerState = rememberPagerState(),
    tabs: List<T>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    pageSize: PageSize = PageSize.Fill,
    pageSpacing: Dp = 0.dp,
    onTabShow: @Composable (tab: T) -> Unit,
) {
    val scope = rememberCoroutineScope()
    Column(modifier = modifier.fillMaxSize()) {
        TabList(
            tabs = tabs,
            currentTabItem = tabs.getOrNull(state.currentPage) ?: tabs[0],
            pagerState = state,
            onTabSelected = { tab ->
                val tabIndex = tabs.indexOf(tab)
                scope.launch { state.animateScrollToPage(tabIndex) }
            },
        )
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            pageCount = tabs.size,
            state = state,
            contentPadding = contentPadding,
            pageSize = pageSize,
            pageSpacing = pageSpacing,
        ) { page ->
            tabs.getOrNull(page)?.let { tab ->
                onTabShow(tab)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun <T : TabItem> TabList(
    modifier: Modifier = Modifier,
    tabs: List<T>,
    pagerState: PagerState? = null,
    currentTabItem: T,
    onTabSelected: (T) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.background,
) {
    TabRow(
        selectedTabIndex = tabs.indexOf(currentTabItem),
        modifier = modifier.fillMaxWidth(),
        containerColor = containerColor,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = when (pagerState != null) {
                    true -> Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    false -> Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(currentTabItem)])
                },
            )
        },
        divider = {
            Divider(color = MaterialTheme.colorScheme.surfaceTwo())
        },
    ) {
        tabs.forEachIndexed { index, tab ->
            LeadingIconTab(
                selected = tabs.indexOf(currentTabItem) == index,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = tab.leadingIcon),
                        contentDescription = tab.title,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                },
                text = {
                    val textColor = when (tabs.indexOf(currentTabItem) == index) {
                        true -> MaterialTheme.colorScheme.onSurface
                        false -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(
                        text = tab.title,
                        color = textColor,
                        style = MaterialTheme.typography.titleSmall,
                    )
                },
            )
        }
    }
}

interface TabItem {
    val title: String @Composable get
    val leadingIcon: Int @Composable get
}

@ExperimentalFoundationApi
fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
): Modifier = composed {
    val targetIndicatorOffset: Dp
    val indicatorWidth: Dp

    val currentTab = tabPositions[pagerState.settledPage]
    var isRightMove by remember { mutableStateOf(false) }
    val targetPage = if (pagerState.currentPageOffsetFraction != 0f) {
        val initValue = remember { pagerState.currentPageOffsetFraction }
        if (initValue >= 0) {
            isRightMove = true
            pagerState.settledPage + 1
        } else {
            isRightMove = false
            pagerState.settledPage - 1
        }
    } else {
        pagerState.currentPage
    }
    val targetTab = tabPositions.getOrNull(targetPage)
    if (targetTab != null) {
        val pageFraction = pagerState.currentPageOffsetFraction
        val fraction = when (pageFraction > 0f) {
            true -> pageFraction
            false -> if (pageFraction == 0f && !isRightMove) 0f else 1 - pageFraction.absoluteValue
        }
        targetIndicatorOffset = when (isRightMove) {
            true -> lerp(currentTab.left, targetTab.left, fraction)
            false -> lerp(targetTab.left, currentTab.left, fraction)
        }
        indicatorWidth = lerp(currentTab.width, targetTab.width, fraction)
    } else {
        targetIndicatorOffset = currentTab.left
        indicatorWidth = currentTab.width
    }

    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = targetIndicatorOffset)
        .width(indicatorWidth)
}

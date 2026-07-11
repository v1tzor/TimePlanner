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
package ru.aleshin.features.home.impl.presentation.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.core.utils.managers.LocalDrawerManager
import ru.aleshin.core.utils.navigation.backAnimation
import ru.aleshin.features.home.impl.presentation.theme.HomeTheme
import ru.aleshin.features.home.impl.presentation.ui.categories.CategoriesContent
import ru.aleshin.features.home.impl.presentation.ui.details.DetailsContent
import ru.aleshin.features.home.impl.presentation.ui.home.HomeContent
import ru.aleshin.features.home.impl.presentation.ui.overview.OverviewContent
import ru.aleshin.features.home.impl.presentation.ui.templates.TemplatesContent

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal class HomeContentProvider(
    private val homeComponent: InternalHomeFeatureComponent,
) : FeatureContentProvider {

    @Composable
    @OptIn(ExperimentalDecomposeApi::class)
    override fun invoke(modifier: Modifier) {
        HomeTheme {
            val drawerManager = LocalDrawerManager.current
            ChildStack(
                stack = homeComponent.stack,
                animation = backAnimation(
                    backHandler = homeComponent.backHandler,
                    onBack = homeComponent::navigateToBack,
                ),
            ) { child ->
                when (val instance = child.instance) {
                    is InternalHomeFeatureComponent.Child.CategoriesChild -> {
                        CategoriesContent(instance.component, Modifier)
                    }
                    is InternalHomeFeatureComponent.Child.DetailsChild -> {
                        DetailsContent(instance.component, Modifier)
                    }
                    is InternalHomeFeatureComponent.Child.HomeChild -> {
                        HomeContent(instance.component, Modifier)
                    }
                    is InternalHomeFeatureComponent.Child.OverviewChild -> {
                        OverviewContent(instance.component, Modifier)
                    }
                    is InternalHomeFeatureComponent.Child.TemplatesChild -> {
                        TemplatesContent(instance.component, Modifier)
                    }
                }
                LaunchedEffect(child) {
                    val screenIndex = fetchFeatureScreenIndex(child.instance)
                    drawerManager?.changeItem(screenIndex)
                }
            }
            LaunchedEffect(Unit) {
                homeComponent.setDrawerManager(drawerManager)
            }
        }
    }
}

internal fun fetchFeatureScreenIndex(screen: InternalHomeFeatureComponent.Child) = when (screen) {
    is InternalHomeFeatureComponent.Child.HomeChild -> 0
    is InternalHomeFeatureComponent.Child.OverviewChild -> 1
    is InternalHomeFeatureComponent.Child.DetailsChild -> 1
    is InternalHomeFeatureComponent.Child.TemplatesChild -> 2
    is InternalHomeFeatureComponent.Child.CategoriesChild -> 3
}

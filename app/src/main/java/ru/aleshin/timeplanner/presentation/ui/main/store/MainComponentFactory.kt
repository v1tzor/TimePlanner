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

package ru.aleshin.timeplanner.presentation.ui.main.store

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.features.editor.api.EditorDecomposeFeatureFactory
import ru.aleshin.features.home.api.HomeDecomposeFeatureFactory
import ru.aleshin.timeplanner.presentation.ui.main.contract.DeepLinkTarget
import ru.aleshin.timeplanner.presentation.ui.main.contract.ShareTarget
import ru.aleshin.timeplanner.presentation.ui.tabs.store.TabNavigationComponentFactory
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 26.08.2025.
 */
interface MainComponentFactory {

    fun createComponent(
        componentContext: ComponentContext,
        initialDeepLinkTarget: DeepLinkTarget?,
        initialShareTarget: ShareTarget?,
    ): MainComponent

    class Default @Inject constructor(
        private val mainStoreFactory: MainComposeStore.Factory,
        private val homeFeatureFactory: HomeDecomposeFeatureFactory,
        private val navigationComponentFactory: TabNavigationComponentFactory,
        private val editorFeatureFactory: EditorDecomposeFeatureFactory,
    ) : MainComponentFactory {

        override fun createComponent(
            componentContext: ComponentContext,
            initialDeepLinkTarget: DeepLinkTarget?,
            initialShareTarget: ShareTarget?,
        ): MainComponent {
            return MainComponent.Base(
                mainStoreFactory = mainStoreFactory,
                componentContext = componentContext,
                initialDeepLinkTarget = initialDeepLinkTarget,
                initialShareTarget = initialShareTarget,
                homeFeatureFactory = homeFeatureFactory,
                navigationComponentFactory = navigationComponentFactory,
                editorFeatureFactory = editorFeatureFactory,
            )
        }
    }
}

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
package ru.aleshin.features.editor.impl.presentation.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.core.utils.navigation.backAnimation
import ru.aleshin.features.editor.impl.presentation.theme.EditorTheme
import ru.aleshin.features.editor.impl.presentation.ui.categories.CategoriesContent
import ru.aleshin.features.editor.impl.presentation.ui.root.InternalEditorFeatureComponent.Child
import ru.aleshin.features.editor.impl.presentation.ui.task.TaskContent

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal class EditorContentProvider(
    private val component: InternalEditorFeatureComponent,
) : FeatureContentProvider {

    @Composable
    @OptIn(ExperimentalDecomposeApi::class)
    override fun invoke(modifier: Modifier) {
        EditorTheme {
            ChildStack(
                modifier = modifier,
                stack = component.stack,
                animation = backAnimation(
                    backHandler = component.backHandler,
                    onBack = component::navigateToBack
                )
            ) { child ->
                when (val instance = child.instance) {
                    is Child.TaskChild -> {
                        TaskContent(taskComponent = instance.component)
                    }
                    is Child.CategoriesChild -> {
                        CategoriesContent(categoriesComponent = instance.component)
                    }
                }
            }
        }
    }
}

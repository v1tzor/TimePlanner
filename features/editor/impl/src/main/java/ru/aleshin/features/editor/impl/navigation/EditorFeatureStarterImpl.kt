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
package ru.aleshin.features.editor.impl.navigation

import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.features.editor.api.navigations.EditorFeatureStarter
import ru.aleshin.features.editor.impl.domain.common.convertToEditModel
import ru.aleshin.features.editor.impl.domain.interactors.EditorInteractor
import ru.aleshin.features.home.api.domains.entities.schedules.TimeTask
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
internal class EditorFeatureStarterImpl @Inject constructor(
    private val editorScreen: Screen,
    private val editorInteractor: EditorInteractor,
) : EditorFeatureStarter {

    override fun provideMainScreen(
        timeTask: TimeTask,
        templateId: Int?,
    ) = editorInteractor.sendEditModel(timeTask.convertToEditModel(templateId)).let {
        editorScreen
    }
}

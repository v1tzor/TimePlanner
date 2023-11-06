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
package ru.aleshin.features.editor.impl.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.common.EditorErrorHandler
import ru.aleshin.features.editor.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.EditorInteractor
import ru.aleshin.features.editor.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.TimeTaskInteractor
import ru.aleshin.features.editor.impl.domain.interactors.UndefinedTasksInteractor

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
@Module
internal interface DomainModule {

    @Binds
    fun bindEditorInteractor(interactor: EditorInteractor.Base): EditorInteractor

    @Binds
    fun bindTimeTaskInteractor(interactor: TimeTaskInteractor.Base): TimeTaskInteractor

    @Binds
    fun bindTemplatesInteractor(interactor: TemplatesInteractor.Base): TemplatesInteractor

    @Binds
    fun bindUndefinedTasksInteractor(interactor: UndefinedTasksInteractor.Base): UndefinedTasksInteractor

    @Binds
    fun bindCategoriesInteractor(interactor: CategoriesInteractor.Base): CategoriesInteractor

    @Binds
    fun bindHomeEitherWrapper(wrapper: EditorEitherWrapper.Base): EditorEitherWrapper

    @Binds
    fun bindHomeErrorHandler(handler: EditorErrorHandler.Base): EditorErrorHandler
}

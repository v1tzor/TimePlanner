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
package ru.aleshin.features.editor.impl.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.editor.impl.data.datasources.EditorLocalDataSource
import ru.aleshin.features.editor.impl.data.repositories.EditorRepositoryImpl
import ru.aleshin.features.editor.impl.domain.repositories.EditorRepository

/**
 * @author Stanislav Aleshin on 10.03.2023.
 */
@Module
internal interface DataModule {

    @Binds
    @FeatureScope
    fun bindEditorRepository(repository: EditorRepositoryImpl): EditorRepository

    @Binds
    @FeatureScope
    fun bindEditorLocalDataSource(dataSource: EditorLocalDataSource.Base): EditorLocalDataSource
}

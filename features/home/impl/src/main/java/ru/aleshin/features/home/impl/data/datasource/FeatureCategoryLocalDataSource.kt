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
package ru.aleshin.features.home.impl.data.datasource

import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 05.11.2023.
 */
internal interface FeatureCategoryLocalDataSource {

    suspend fun fetchMainCategoryId(): Int?

    fun setMainCategoryId(id: Int?)

    class Base @Inject constructor() : FeatureCategoryLocalDataSource {

        private var mainCategoryId: Int? = null

        override suspend fun fetchMainCategoryId(): Int? {
            return mainCategoryId
        }

        override fun setMainCategoryId(id: Int?) {
            this.mainCategoryId = id
        }
    }
}

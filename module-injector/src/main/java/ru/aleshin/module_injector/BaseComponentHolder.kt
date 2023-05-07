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
package ru.aleshin.module_injector

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
interface BaseComponentHolder<A : BaseFeatureApi, D : BaseFeatureDependencies> {

    /**
     * Initializes the internal DI graph to be able to get the API features.
     *
     * @param dependencies needed for the features to work
     */
    fun init(dependencies: D)

    /**
     * Allows get API for this features if DI graph is initialize.
     *
     * @return [A] API for working with features
     *
     * @exception IllegalStateException if DI graph is not initialized.
     */
    fun fetchApi(): A

    /**
     * Deleting the internal DI graph to close feature.
     */
    fun clear()
}

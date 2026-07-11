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
package ru.aleshin.core.utils.managers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
interface CoroutineManager : WorkDispatchersProvider {

    fun runOnIOBackground(scope: CoroutineScope, block: CoroutineBlock): Job

    fun runOnDefaultBackground(scope: CoroutineScope, block: CoroutineBlock): Job

    fun runOnUi(scope: CoroutineScope, block: CoroutineBlock): Job

    suspend fun changeFlow(coroutineFlow: CoroutineFlow, block: CoroutineBlock)

    abstract class Abstract(
        override val defaultDispatcher: CoroutineDispatcher,
        override val ioDispatcher: CoroutineDispatcher,
        override val uiDispatcher: CoroutineDispatcher,
    ) : CoroutineManager {

        override fun runOnIOBackground(scope: CoroutineScope, block: CoroutineBlock): Job {
            return scope.launch(context = ioDispatcher, block = block)
        }

        override fun runOnDefaultBackground(scope: CoroutineScope, block: CoroutineBlock): Job {
            return scope.launch(context = defaultDispatcher, block = block)
        }

        override fun runOnUi(scope: CoroutineScope, block: CoroutineBlock): Job {
            return scope.launch(context = uiDispatcher, block = block)
        }

        override suspend fun changeFlow(coroutineFlow: CoroutineFlow, block: CoroutineBlock) {
            val dispatcher = when (coroutineFlow) {
                CoroutineFlow.DEFAULT -> defaultDispatcher
                CoroutineFlow.IO -> ioDispatcher
                CoroutineFlow.UI -> uiDispatcher
            }
            withContext(context = dispatcher, block = block)
        }
    }

    class Base @Inject constructor() : Abstract(
        defaultDispatcher = Dispatchers.Main,
        ioDispatcher = Dispatchers.IO,
        uiDispatcher = Dispatchers.Main,
    )
}

interface WorkDispatchersProvider {
    val ioDispatcher: CoroutineDispatcher
    val defaultDispatcher: CoroutineDispatcher
    val uiDispatcher: CoroutineDispatcher
}

typealias CoroutineBlock = suspend CoroutineScope.() -> Unit

enum class CoroutineFlow {
    DEFAULT, IO, UI
}

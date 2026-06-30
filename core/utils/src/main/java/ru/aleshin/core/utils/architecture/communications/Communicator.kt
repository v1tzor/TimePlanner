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
package ru.aleshin.core.utils.architecture.communications

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

/**
 * @author Stanislav Aleshin on 12.06.2023.
 */
interface Communicator<T> {

    suspend fun collect(collector: FlowCollector<T>)

    suspend fun read(): T

    fun update(data: T)

    abstract class AbstractStateFlow<T>(defaultValue: T) : Communicator<T> {

        protected val flow = MutableStateFlow(value = defaultValue)

        override suspend fun collect(collector: FlowCollector<T>) {
            flow.collect(collector)
        }

        fun getValue() = flow.value

        override suspend fun read(): T = getValue()

        override fun update(data: T) {
            flow.update { data }
        }
    }

    abstract class AbstractSharedFlow<T>(
        flowReplay: Int = 0,
        flowBufferCapacity: Int = 0,
        flowBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
    ) : Communicator<T> {

        protected val flow = MutableSharedFlow<T>(
            replay = flowReplay,
            extraBufferCapacity = flowBufferCapacity,
            onBufferOverflow = flowBufferOverflow,
        )

        override suspend fun collect(collector: FlowCollector<T>) {
            flow.collect(collector)
        }

        override suspend fun read(): T {
            return flow.first()
        }

        override fun update(data: T) {
            flow.tryEmit(data)
        }
    }
}
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

package ru.aleshin.core.utils.architecture.component

/**
 * @author Stanislav Aleshin on 16.08.2025.
 */
fun interface OutputConsumer<O : BaseOutput> {
    fun consume(data: O)
}

object EmptyOutputConsumer : OutputConsumer<EmptyOutput> {
    override fun consume(data: EmptyOutput) = Unit
}

typealias OutputConsumerProvider<O> = () -> OutputConsumer<O>

val EmptyOutputConsumerProvider = { EmptyOutputConsumer }
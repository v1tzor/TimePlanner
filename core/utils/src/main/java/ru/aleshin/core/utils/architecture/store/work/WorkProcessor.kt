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
package ru.aleshin.core.utils.architecture.store.work

import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect

/**
 * @author Stanislav Aleshin on 12.06.2023.
 */
interface WorkProcessor<C : WorkCommand, A : StoreAction, E : StoreEffect, O : BaseOutput> {
    suspend fun work(command: C): WorkResult<A, E, O>
}

interface WorkCommand : BackgroundWorkKey

sealed class WorkResult<out A : StoreAction, out E : StoreEffect, out O : BaseOutput> {
    data class Action<A : StoreAction>(val action: A) : WorkResult<A, Nothing, Nothing>()
    data class Effect<E : StoreEffect>(val effect: E) : WorkResult<Nothing, E, Nothing>()
    data class Output<O : BaseOutput>(val output: O) : WorkResult<Nothing, Nothing, O>()
}

typealias ActionResult<A> = WorkResult.Action<A>
typealias EffectResult<F> = WorkResult.Effect<F>
typealias OutputResult<O> = WorkResult.Output<O>
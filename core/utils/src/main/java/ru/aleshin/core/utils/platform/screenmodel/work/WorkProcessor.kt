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
package ru.aleshin.core.utils.platform.screenmodel.work

import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseAction
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseUiEffect

/**
 * @author Stanislav Aleshin on 19.03.2023.
 */
interface WorkProcessor<C : WorkCommand, A : BaseAction, E : BaseUiEffect> {
    suspend fun work(command: C): WorkResult<A, E>
}

interface WorkCommand : BackgroundWorkKey

typealias WorkResult<A, E> = Either<A, E>

typealias ActionResult<A> = Either.Left<A>
typealias EffectResult<F> = Either.Right<F>

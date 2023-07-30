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
package ru.aleshin.core.utils.wrappers

import kotlinx.coroutines.flow.*
import ru.aleshin.core.utils.functional.DomainFailures
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.handlers.ErrorHandler

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
interface EitherWrapper<F : DomainFailures> {

    suspend fun <O> wrap(block: suspend () -> O): Either<F, O>

    abstract class Abstract<F : DomainFailures>(
        private val errorHandler: ErrorHandler<F>,
    ) : EitherWrapper<F> {

        override suspend fun <O> wrap(block: suspend () -> O) = try {
            Either.Right(data = block.invoke())
        } catch (error: Throwable) {
            Either.Left(data = errorHandler.handle(error))
        }
    }
}

interface FlowEitherWrapper<F : DomainFailures> : EitherWrapper<F> {

    suspend fun <O> wrapFlow(block: suspend () -> Flow<O>): Flow<Either<F, O>>

    abstract class Abstract<F : DomainFailures>(
        private val errorHandler: ErrorHandler<F>,
    ) : FlowEitherWrapper<F>, EitherWrapper.Abstract<F>(errorHandler) {

        override suspend fun <O> wrapFlow(block: suspend () -> Flow<O>) = flow {
            block.invoke()
                .catch { error -> this@flow.emit(Either.Left(data = errorHandler.handle(error))) }
                .collect { data -> emit(Either.Right(data = data)) }
        }
    }
}

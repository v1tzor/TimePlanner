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
package ru.aleshin.core.utils.functional

import kotlinx.coroutines.flow.Flow

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
sealed class Either<out L, out R> {

    data class Left<L>(val data: L) : Either<L, Nothing>()

    data class Right<R>(val data: R) : Either<Nothing, R>()

    val isLeft = this is Left

    val isRight = this is Right
}

typealias DomainResult<L, R> = Either<L, R>

typealias FlowDomainResult<L, R> = Flow<Either<L, R>>

typealias UnitDomainResult<L> = Either<L, Unit>

fun <L, R> Either<L, R>.rightOrElse(elseValue: R): R = when (this) {
    is Either.Left -> elseValue
    is Either.Right -> this.data
}

fun <L, R> Either<L, R>.leftOrElse(elseValue: L): L = when (this) {
    is Either.Left -> this.data
    is Either.Right -> elseValue
}

suspend fun <L, R> Either<L, R>.handle(
    onLeftAction: suspend (L) -> Unit = {},
    onRightAction: suspend (R) -> Unit = {},
) = when (this) {
    is Either.Left -> onLeftAction(this.data)
    is Either.Right -> onRightAction(this.data)
}

suspend fun <L, R, T> Either<L, R>.handleAndGet(
    onLeftAction: suspend (L) -> T,
    onRightAction: suspend (R) -> T,
) = when (this) {
    is Either.Left -> onLeftAction(this.data)
    is Either.Right -> onRightAction(this.data)
}

suspend fun <L, R> Either<L, R>.rightOrError(message: String) = handleAndGet(
    onLeftAction = { error(message) },
    onRightAction = { it },
)

suspend fun <L, R> Flow<Either<L, R>>.collectAndHandle(
    onLeftAction: suspend (L) -> Unit = {},
    onRightAction: suspend (R) -> Unit = {},
) = collect { either ->
    either.handle(onLeftAction, onRightAction)
}
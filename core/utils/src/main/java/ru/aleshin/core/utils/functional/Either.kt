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
package ru.aleshin.core.utils.functional

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
sealed class Either<out L, out R> {

    data class Left<L>(val data: L) : Either<L, Nothing>()

    data class Right<R>(val data: R) : Either<Nothing, R>()

    val isLeft = this is Left

    val isRight = this is Right

    fun <LT, RT> map(
        leftMapper: (L) -> LT,
        rightMapper: (R) -> RT,
    ): Either<LT, RT> = when (this) {
        is Left -> Left(leftMapper(data))
        is Right -> Right(rightMapper(this.data))
    }

    fun <T> mapLeft(mapper: (L) -> T) = map(
        leftMapper = mapper,
        rightMapper = { it },
    )

    fun <T> mapRight(mapper: (R) -> T) = map(
        leftMapper = { it },
        rightMapper = mapper,
    )
}

inline fun <L, R> Either<L, R>.rightOrElse(
    elseValue: R
): R = when (this) {
    is Either.Left -> elseValue
    is Either.Right -> this.data
}

suspend fun <L, R> Either<L, R>.rightOrNull(
    onLeftAction: suspend (L) -> Unit = {},
): R? = when (this) {
    is Either.Left -> onLeftAction(this.data).let { null }
    is Either.Right -> this.data
}

suspend fun <L, R> Flow<Either<L, R>>.firstRightOrNull(
    onLeftAction: suspend (L) -> Unit = {},
): R? = when (val firstValue = first()) {
    is Either.Left -> onLeftAction(firstValue.data).let { null }
    is Either.Right -> firstValue.data
}

inline fun <L, R> Either<L, R>.leftOrElse(
    elseValue: L
): L = when (this) {
    is Either.Left -> this.data
    is Either.Right -> elseValue
}

suspend fun <L, R> Either<L, R>.leftOrNull(
    onRightAction: suspend (R) -> Unit = {},
): L? = when (this) {
    is Either.Left -> this.data
    is Either.Right -> onRightAction(this.data).let { null }
}

suspend fun <L, R> Either<L, R>.handle(
    onLeftAction: suspend (L) -> Unit = {},
    onRightAction: suspend (R) -> Unit = {},
) = when (this) {
    is Either.Left -> onLeftAction.invoke(this.data)
    is Either.Right -> onRightAction.invoke(this.data)
}

suspend fun <L, R, T> Either<L, R>.handleAndGet(
    onLeftAction: suspend (L) -> T,
    onRightAction: suspend (R) -> T,
) = when (this) {
    is Either.Left -> onLeftAction.invoke(this.data)
    is Either.Right -> onRightAction.invoke(this.data)
}

suspend inline fun <L, R, T> Flow<Either<L, R>>.firstOrNullHandleAndGet(
    onLeftAction: suspend (L) -> T,
    onRightAction: suspend (R) -> T,
): T? = when (val firstValue = firstOrNull()) {
    is Either.Left -> onLeftAction.invoke(firstValue.data)
    is Either.Right -> onRightAction.invoke(firstValue.data)
    else -> null
}

suspend fun <L, R, T> Flow<Either<L, R>>.firstHandleAndGet(
    onLeftAction: suspend (L) -> T,
    onRightAction: suspend (R) -> T,
) = when (val firstValue = first()) {
    is Either.Left -> onLeftAction.invoke(firstValue.data)
    is Either.Right -> onRightAction.invoke(firstValue.data)
}

suspend fun <L, R> Flow<Either<L, R>>.collectAndHandle(
    onLeftAction: suspend (L) -> Unit = {},
    onRightAction: suspend (R) -> Unit = {},
) = collect { either -> either.handle(onLeftAction, onRightAction) }

typealias UnitDomainResult<L> = Either<L, Unit>

typealias DomainResult<L, R> = Either<L, R>

typealias FlowDomainResult<L, R> = Flow<Either<L, R>>
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

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
sealed class ResponseResult<out T> {

    abstract fun <O> map(dataTransform: (T) -> O): ResponseResult<O>

    sealed class Success<T> : ResponseResult<T>() {

        abstract val data: T?

        abstract val code: Int

        data class Data<T>(override val data: T, override val code: Int) : Success<T>() {
            override fun <O> map(dataTransform: (T) -> O) = Data(dataTransform.invoke(data), code)
        }

        data class Empty<T>(override val data: T?, override val code: Int) : Success<T>() {
            override fun <O> map(dataTransform: (T) -> O) = Empty<O>(null, code)
        }
    }

    data class Error(val throwable: Throwable) : ResponseResult<Nothing>() {
        override fun <O> map(dataTransform: (Nothing) -> O) = this
    }
}

fun <T> ResponseResult<T>.dataOrThrow(): T? = when (this) {
    is ResponseResult.Success.Data -> data
    is ResponseResult.Success.Empty -> null
    is ResponseResult.Error -> throw throwable
}

fun <T> ResponseResult<T>.codeOrThrow(): Int = when (this) {
    is ResponseResult.Success.Data -> code
    is ResponseResult.Success.Empty -> code
    is ResponseResult.Error -> throw throwable
}

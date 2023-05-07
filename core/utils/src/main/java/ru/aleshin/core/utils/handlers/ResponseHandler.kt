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
package ru.aleshin.core.utils.handlers

import retrofit2.HttpException
import retrofit2.Response
import ru.aleshin.core.utils.functional.Mapper
import ru.aleshin.core.utils.functional.ResponseResult
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
interface ResponseHandler {

    suspend fun <T> handle(
        block: suspend () -> Response<T>,
    ): ResponseResult<T>

    suspend fun <T, O> handleWithMap(
        mapper: Mapper<T, O>,
        block: suspend () -> Response<T>,
    ): ResponseResult<O>

    class Base @Inject constructor() : ResponseHandler {

        override suspend fun <T, O> handleWithMap(
            mapper: Mapper<T, O>,
            block: suspend () -> Response<T>,
        ) = handle(block).map { mapper.map(it) }

        override suspend fun <T> handle(
            block: suspend () -> Response<T>,
        ) = try {
            val response = block.invoke()
            if (response.isSuccessful) {
                if (response.body() == null || response.body().toString().isEmpty()) {
                    ResponseResult.Success.Empty(null, response.code())
                } else {
                    ResponseResult.Success.Data(checkNotNull(response.body()), response.code())
                }
            } else {
                ResponseResult.Error(HttpException(response))
            }
        } catch (e: Throwable) {
            ResponseResult.Error(e)
        }
    }
}

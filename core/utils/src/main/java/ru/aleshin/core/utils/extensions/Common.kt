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
package ru.aleshin.core.utils.extensions

import java.util.UUID

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
fun <T> List<T>.equalsIgnoreOrder(other: List<T>) =
    this.size == other.size && this.toSet() == other.toSet()

fun <T> List<List<T>>.extractAllItem() = mutableListOf<T>().apply {
    this@extractAllItem.forEach { addAll(it) }
}

fun Int?.toStringOrEmpty() = this?.toString() ?: ""

fun generateUniqueKey() = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE

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
package ru.aleshin.core.data.models.template

/**
 * @author Stanislav Aleshin on 03.08.2023.
 */
data class TemplateCompoundEntity(
    val template: TemplateEntity,
    val repeatTimes: List<RepeatTimeEntity>,
)

fun List<TemplateCompoundEntity>.allRepeatTimes() = mutableListOf<RepeatTimeEntity>().apply {
    this@allRepeatTimes.map { it.repeatTimes }.forEach { addAll(it) }
}

fun List<TemplateCompoundEntity>.allTemplatesId() = map { it.template.id }
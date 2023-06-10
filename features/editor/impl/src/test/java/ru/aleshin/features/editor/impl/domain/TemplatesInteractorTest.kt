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
 * imitations under the License.
 */
package ru.aleshin.features.editor.impl.domain

import android.database.sqlite.SQLiteException
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.utils.extensions.endThisDay
import ru.aleshin.core.utils.extensions.setStartDay
import ru.aleshin.core.utils.extensions.shiftMinutes
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.common.EditorErrorHandler
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.template.Template
import ru.aleshin.features.home.api.domains.repository.TemplatesRepository
import java.util.Calendar
import kotlin.NullPointerException

/**
 * @author Stanislav Aleshin on 01.06.2023.
 */
internal class TemplatesInteractorTest {

    private lateinit var interactor: TemplatesInteractor
    private lateinit var templatesRepository: FakeTemplatesRepository
    private lateinit var eitherWrapper: EditorEitherWrapper
    private lateinit var errorHandler: EditorErrorHandler

    @Before
    fun setUp() {
        templatesRepository = FakeTemplatesRepository()
        errorHandler = EditorErrorHandler.Base()
        eitherWrapper = EditorEitherWrapper.Base(errorHandler)

        interactor = TemplatesInteractor.Base(
            templatesRepository = templatesRepository,
            eitherWrapper = eitherWrapper,
        )
    }

    @Test
    fun test_add_template_success() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        templatesRepository.templatesList.add(
            Template(
                templateId = 100,
                startTime = fakeTime.startThisDay(),
                endTime = fakeTime.endThisDay(),
                category = MainCategory.absent(),
            ),
        )

        val addedTemplate = Template(
            templateId = 0,
            startTime = fakeTime.shiftMinutes(10),
            endTime = fakeTime.shiftMinutes(20),
            category = MainCategory.absent(),
        )

        val actual = interactor.addTemplate(addedTemplate)

        assertEquals(true, actual.isRight)

        assertEquals(1, templatesRepository.addedTemplatesCount)
        assertEquals(2, templatesRepository.templatesList.size)
        // Auto template id by order
        assertEquals(addedTemplate.copy(templateId = 101), templatesRepository.templatesList[1])
    }

    @Test
    fun test_add_template_with_error() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        templatesRepository.templatesList.add(
            Template(
                templateId = 100,
                startTime = fakeTime.startThisDay(),
                endTime = fakeTime.endThisDay(),
                category = MainCategory.absent(),
            ),
        )
        templatesRepository.errorWhileAction = true

        val addedTemplate = Template(
            templateId = 101,
            startTime = fakeTime.shiftMinutes(10),
            endTime = fakeTime.shiftMinutes(20),
            category = MainCategory.absent(),
        )

        val actual = interactor.addTemplate(addedTemplate)

        assertEquals(true, actual.isLeft)
        assertEquals(true, (actual as Either.Left).data is EditorFailures.OtherError)

        assertEquals(1, templatesRepository.addedTemplatesCount)
        assertEquals(1, templatesRepository.templatesList.size)
    }

    @Test
    fun test_fetch_templates_success() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        templatesRepository.templatesList.add(
            Template(
                templateId = 100,
                startTime = fakeTime.startThisDay(),
                endTime = fakeTime.endThisDay(),
                category = MainCategory.absent(),
            ),
        )

        val actual = interactor.fetchTemplates()
        val expected = listOf(
            Template(
                templateId = 100,
                startTime = fakeTime.startThisDay(),
                endTime = fakeTime.endThisDay(),
                category = MainCategory.absent(),
            ),
        )

        assertEquals(true, actual.isRight)
        assertEquals(expected, (actual as Either.Right).data)

        assertEquals(1, templatesRepository.fetchTemplatesCount)
        assertEquals(1, templatesRepository.templatesList.size)
    }

    @Test
    fun test_fetch_templates_with_error() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        templatesRepository.templatesList.add(
            Template(
                templateId = 100,
                startTime = fakeTime.startThisDay(),
                endTime = fakeTime.endThisDay(),
                category = MainCategory.absent(),
            ),
        )
        templatesRepository.errorWhileAction = true

        val actual = interactor.fetchTemplates()

        assertEquals(true, actual.isLeft)
        assertEquals(true, (actual as Either.Left).data is EditorFailures.OtherError)

        assertEquals(1, templatesRepository.fetchTemplatesCount)
        assertEquals(1, templatesRepository.templatesList.size)
    }

    @Test
    fun test_update_template_success() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        templatesRepository.templatesList.add(
            Template(
                templateId = 100,
                startTime = fakeTime.startThisDay(),
                endTime = fakeTime.endThisDay(),
                category = MainCategory.absent(),
            ),
        )

        val updatedTemplate = Template(
            templateId = 100,
            startTime = fakeTime.shiftMinutes(10),
            endTime = fakeTime.endThisDay(),
            category = MainCategory.absent(),
        )

        val actual = interactor.updateTemplate(updatedTemplate)

        assertEquals(true, actual.isRight)

        assertEquals(1, templatesRepository.updateTemplatesCount)
        assertEquals(1, templatesRepository.templatesList.size)
        assertEquals(updatedTemplate, templatesRepository.templatesList[0])
    }

    @Test
    fun test_update_template_with_error() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        templatesRepository.templatesList.add(
            Template(
                templateId = 100,
                startTime = fakeTime.startThisDay(),
                endTime = fakeTime.endThisDay(),
                category = MainCategory.absent(),
            ),
        )
        templatesRepository.errorWhileAction = true

        val updatedTemplate = Template(
            templateId = 100,
            startTime = fakeTime.shiftMinutes(10),
            endTime = fakeTime.endThisDay(),
            category = MainCategory.absent(),
        )

        val actual = interactor.updateTemplate(updatedTemplate)

        assertEquals(true, actual.isLeft)
        assertEquals(true, (actual as Either.Left).data is EditorFailures.OtherError)

        assertEquals(1, templatesRepository.updateTemplatesCount)
        assertEquals(1, templatesRepository.templatesList.size)
    }

    @Test
    fun test_delete_template_success() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        templatesRepository.templatesList.add(
            Template(
                templateId = 100,
                startTime = fakeTime.startThisDay(),
                endTime = fakeTime.endThisDay(),
                category = MainCategory.absent(),
            ),
        )

        val actual = interactor.deleteTemplateById(100)

        assertEquals(true, actual.isRight)

        assertEquals(1, templatesRepository.deleteTemplatesCount)
        assertEquals(0, templatesRepository.templatesList.size)
    }

    @Test
    fun test_delete_template_with_error() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        templatesRepository.templatesList.add(
            Template(
                templateId = 100,
                startTime = fakeTime.startThisDay(),
                endTime = fakeTime.endThisDay(),
                category = MainCategory.absent(),
            ),
        )
        templatesRepository.errorWhileAction = true

        val actual = interactor.deleteTemplateById(100)

        assertEquals(true, actual.isLeft)
        assertEquals(true, (actual as Either.Left).data is EditorFailures.OtherError)

        assertEquals(1, templatesRepository.deleteTemplatesCount)
        assertEquals(1, templatesRepository.templatesList.size)
    }
}

private class FakeTemplatesRepository : TemplatesRepository {

    val templatesList = mutableListOf<Template>()

    var addedTemplatesCount = 0
    var fetchTemplatesCount = 0
    var updateTemplatesCount = 0
    var deleteTemplatesCount = 0

    var errorWhileAction = false

    override suspend fun addTemplate(templates: Template): Int {
        addedTemplatesCount++
        return if (!errorWhileAction) {
            val templateId = templatesList.lastOrNull()?.templateId?.inc() ?: 0
            templatesList.add(templates.copy(templateId = templateId)).let { templateId }
        } else {
            throw SQLiteException()
        }
    }

    override suspend fun addTemplates(templates: List<Template>) {
        addedTemplatesCount++
        if (!errorWhileAction) {
            templatesList.addAll(templates)
        } else {
            throw SQLiteException()
        }
    }

    override suspend fun fetchAllTemplates(): List<Template> {
        fetchTemplatesCount++
        return if (!errorWhileAction) {
            templatesList
        } else {
            throw NullPointerException()
        }
    }

    override suspend fun updateTemplate(template: Template) {
        updateTemplatesCount++
        if (!errorWhileAction) {
            val index = templatesList.indexOfFirst { it.templateId == template.templateId }
            templatesList[index] = template
        } else {
            throw SQLiteException()
        }
    }

    override suspend fun deleteTemplateById(id: Int) {
        deleteTemplatesCount++
        if (!errorWhileAction) {
            templatesList.removeAt(templatesList.indexOfFirst { it.templateId == id })
        } else {
            throw SQLiteException()
        }
    }

    override suspend fun deleteAllTemplates() {
        deleteTemplatesCount++
        if (!errorWhileAction) {
            templatesList.clear()
        } else {
            throw SQLiteException()
        }
    }
}

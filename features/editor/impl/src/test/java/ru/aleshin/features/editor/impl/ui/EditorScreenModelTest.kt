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
package ru.aleshin.features.editor.impl.ui

import android.database.sqlite.SQLiteException
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.extensions.endThisDay
import ru.aleshin.core.utils.extensions.shiftMinutes
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.extensions.toMinutes
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.editor.api.presentation.TimeTaskAlarmManager
import ru.aleshin.features.editor.impl.domain.common.convertToTimeTask
import ru.aleshin.features.editor.impl.domain.entites.EditModel
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.EditorInteractor
import ru.aleshin.features.editor.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.TimeTaskInteractor
import ru.aleshin.features.editor.impl.navigation.NavigationManager
import ru.aleshin.features.editor.impl.presentation.mappers.mapToDomain
import ru.aleshin.features.editor.impl.presentation.mappers.mapToUi
import ru.aleshin.features.editor.impl.presentation.models.EditModelUi
import ru.aleshin.features.editor.impl.presentation.models.EditParameters
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEffect
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEvent
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorViewState
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.EditorWorkProcessor
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.TimeTaskWorkProcessor
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.CategoryValidator
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.EditorEffectCommunicator
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.EditorScreenModel
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.EditorStateCommunicator
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.TimeRangeValidator
import ru.aleshin.features.home.api.domains.entities.categories.Categories
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.domains.entities.schedules.TimeTask
import ru.aleshin.features.home.api.domains.entities.template.Template
import java.lang.NullPointerException
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 01.06.2023.
 */
internal class EditorScreenModelTest {

    private lateinit var timeTaskInteractor: FakeTimeTaskInteractor
    private lateinit var editorInteractor: FakeEditorInteractor
    private lateinit var categoriesInteractor: FakeCategoriesInteractor
    private lateinit var templatesInteractor: FakeTemplatesInteractor
    private lateinit var timeTaskAlarmManager: FakeTimeTaskAlarmManager
    private lateinit var navigationManager: FakeNavigationManager
    private lateinit var dateManager: FakeDateManager
    private lateinit var stateCommunicator: FakeEditorStateCommunicator
    private lateinit var effectCommunicator: FakeEditorEffectCommunicator
    private lateinit var coroutineManager: TestCoroutineManager

    private val screenModel: EditorScreenModel by lazy {
        EditorScreenModel(
            timeTaskWorkProcessor = TimeTaskWorkProcessor.Base(
                timeTaskInteractor = timeTaskInteractor,
                templatesInteractor = templatesInteractor,
                timeTaskAlarmManager = timeTaskAlarmManager,
                navigationManager = navigationManager,
                dateManager = dateManager,
            ),
            editorWorkProcessor = EditorWorkProcessor.Base(
                editorInteractor = editorInteractor,
                categoriesInteractor = categoriesInteractor,
                templatesInteractor = templatesInteractor,
                navigationManager = navigationManager,
            ),
            categoryValidator = CategoryValidator.Base(),
            timeRangeValidator = TimeRangeValidator.Base(),
            stateCommunicator = stateCommunicator,
            effectCommunicator = effectCommunicator,
            coroutineManager = coroutineManager,
        )
    }

    @Before
    fun setUp() {
        timeTaskInteractor = FakeTimeTaskInteractor()
        editorInteractor = FakeEditorInteractor()
        categoriesInteractor = FakeCategoriesInteractor()
        templatesInteractor = FakeTemplatesInteractor()
        timeTaskAlarmManager = FakeTimeTaskAlarmManager()
        navigationManager = FakeNavigationManager()
        dateManager = FakeDateManager()
        stateCommunicator = FakeEditorStateCommunicator()
        effectCommunicator = FakeEditorEffectCommunicator()
        coroutineManager = TestCoroutineManager()
    }

    @Test
    fun test_init_success() {
        val date = Calendar.getInstance().time
        val fakeEditModel = EditModel(
            key = 100L,
            date = date,
            startTime = date.startThisDay(),
            endTime = date.endThisDay(),
        )
        val fakeCategories = Categories(
            MainCategory.absent(),
            listOf(SubCategory.absentSubCategory(MainCategory.absent())),
        )
        editorInteractor.editModel = fakeEditModel
        categoriesInteractor.categoriesList.add(fakeCategories)

        screenModel.init()

        assertEquals(1, editorInteractor.fetchedModelCount)
        assertEquals(1, categoriesInteractor.fetchedCategoriesCount)

        assertEquals(0, effectCommunicator.pushEffectList.size)
        assertEquals(0, effectCommunicator.updateEffectCount)

        assertEquals(1, stateCommunicator.updateStateCount)
        assertEquals(2, stateCommunicator.changedStateList.size)
        assertEquals(
            EditorViewState(editModel = null, categories = listOf()),
            stateCommunicator.changedStateList[0],
        )
        assertEquals(
            EditorViewState(
                editModel = fakeEditModel.mapToUi(),
                categories = listOf(fakeCategories),
            ),
            stateCommunicator.changedStateList[1],
        )
    }

    @Test
    fun test_init_with_failure() {
        val date = Calendar.getInstance().time
        val fakeEditModel = EditModel(
            key = 100L,
            date = date,
            startTime = date.startThisDay(),
            endTime = date.endThisDay(),
        )
        val fakeCategories = Categories(
            MainCategory.absent(),
            listOf(SubCategory.absentSubCategory(MainCategory.absent())),
        )
        editorInteractor.editModel = fakeEditModel
        categoriesInteractor.categoriesList.add(fakeCategories)

        categoriesInteractor.errorWhileAction = true

        screenModel.init()

        assertEquals(1, editorInteractor.fetchedModelCount)
        assertEquals(1, categoriesInteractor.fetchedCategoriesCount)

        assertEquals(1, effectCommunicator.updateEffectCount)
        assertEquals(1, effectCommunicator.pushEffectList.size)
        assertEquals(true, effectCommunicator.pushEffectList[0] is EditorEffect.ShowError)

        assertEquals(0, stateCommunicator.updateStateCount)
        assertEquals(1, stateCommunicator.changedStateList.size)
        assertEquals(
            EditorViewState(editModel = null, categories = listOf()),
            stateCommunicator.changedStateList[0],
        )
    }

    @Test
    fun test_save_edit_model_success() {
        val date = Calendar.getInstance().time
        val fakeMainCategory = MainCategory(id = 1, name = "Work")
        val initEditModel = EditModel(
            key = 100L,
            date = date,
            startTime = date.shiftMinutes(10),
            endTime = date.shiftMinutes(20),
            mainCategory = fakeMainCategory,
        )
        val fakeEditModel = EditModelUi(
            key = 100L,
            date = date,
            timeRanges = TimeRange(from = date.shiftMinutes(20), to = date.endThisDay()),
            duration = duration(date.shiftMinutes(20), date.endThisDay()),
            mainCategory = fakeMainCategory,
        )
        val fakeCategories = Categories(fakeMainCategory, listOf())
        dateManager.currentDate = date.startThisDay()
        timeTaskInteractor.timeTasksList.add(initEditModel.convertToTimeTask())
        timeTaskAlarmManager.notificationTasks.add(initEditModel.convertToTimeTask())
        stateCommunicator.changedStateList.add(
            EditorViewState(editModel = fakeEditModel, categories = listOf(fakeCategories)),
        )

        screenModel.dispatchEvent(EditorEvent.PressSaveButton(false))

        assertEquals(true, navigationManager.isNavigateToHome)

        assertEquals(1, timeTaskAlarmManager.updateNotificationCount)
        assertEquals(1, timeTaskAlarmManager.notificationTasks.size)
        assertEquals(
            fakeEditModel.mapToDomain().convertToTimeTask(),
            timeTaskAlarmManager.notificationTasks[0],
        )

        assertEquals(1, timeTaskInteractor.updateTaskCount)
        assertEquals(1, timeTaskInteractor.timeTasksList.size)
        assertEquals(
            fakeEditModel.mapToDomain().convertToTimeTask(),
            timeTaskInteractor.timeTasksList[0],
        )

        assertEquals(0, templatesInteractor.updateTemplatesCount)

        assertEquals(0, effectCommunicator.pushEffectList.size)
        assertEquals(0, effectCommunicator.updateEffectCount)

        assertEquals(1, stateCommunicator.updateStateCount)
        assertEquals(3, stateCommunicator.changedStateList.size)
        assertEquals(
            EditorViewState(editModel = null, categories = listOf()),
            stateCommunicator.changedStateList[0],
        )
        assertEquals(
            EditorViewState(
                editModel = fakeEditModel,
                categories = listOf(fakeCategories),
            ),
            stateCommunicator.changedStateList[1],
        )
    }

    @Test
    fun test_add_edit_model() {
        val date = Calendar.getInstance().time
        val fakeMainCategory = MainCategory(id = 1, name = "Work")
        val fakeEditModel = EditModelUi(
            key = 0L,
            date = date,
            timeRanges = TimeRange(from = date.shiftMinutes(20), to = date.endThisDay()),
            duration = duration(date.shiftMinutes(20), date.endThisDay()),
            mainCategory = fakeMainCategory,
        )
        val fakeCategories = Categories(fakeMainCategory, listOf())
        dateManager.currentDate = date.startThisDay()
        stateCommunicator.changedStateList.add(
            EditorViewState(editModel = fakeEditModel, categories = listOf(fakeCategories)),
        )

        screenModel.dispatchEvent(EditorEvent.PressSaveButton(false))

        assertEquals(true, navigationManager.isNavigateToHome)

        assertEquals(1, timeTaskAlarmManager.addedNotificationCount)
        assertEquals(1, timeTaskAlarmManager.notificationTasks.size)
        assertEquals(
            fakeEditModel.mapToDomain().convertToTimeTask(),
            timeTaskAlarmManager.notificationTasks[0],
        )

        assertEquals(1, timeTaskInteractor.addedTaskCount)
        assertEquals(1, timeTaskInteractor.timeTasksList.size)
        assertEquals(
            fakeEditModel.mapToDomain().convertToTimeTask(),
            timeTaskInteractor.timeTasksList[0],
        )

        assertEquals(0, templatesInteractor.addedTemplatesCount)

        assertEquals(0, effectCommunicator.pushEffectList.size)
        assertEquals(0, effectCommunicator.updateEffectCount)

        assertEquals(1, stateCommunicator.updateStateCount)
        assertEquals(3, stateCommunicator.changedStateList.size)
        assertEquals(
            EditorViewState(editModel = null, categories = listOf()),
            stateCommunicator.changedStateList[0],
        )
        assertEquals(
            EditorViewState(
                editModel = fakeEditModel,
                categories = listOf(fakeCategories),
            ),
            stateCommunicator.changedStateList[1],
        )
    }

    @Test
    fun test_change_time() {
        val date = Calendar.getInstance().time
        val fakeEditModel = EditModelUi(
            key = 100L,
            date = date,
            timeRanges = TimeRange(from = date.startThisDay(), to = date.endThisDay()),
        )
        val fakeCategories = Categories(
            MainCategory.absent(),
            listOf(SubCategory.absentSubCategory(MainCategory.absent())),
        )
        stateCommunicator.changedStateList.add(
            EditorViewState(editModel = fakeEditModel, categories = listOf(fakeCategories)),
        )

        screenModel.dispatchEvent(
            EditorEvent.ChangeTime(TimeRange(date.shiftMinutes(10), date.shiftMinutes(20))),
        )

        assertEquals(0, effectCommunicator.pushEffectList.size)
        assertEquals(0, effectCommunicator.updateEffectCount)

        assertEquals(1, stateCommunicator.updateStateCount)
        assertEquals(3, stateCommunicator.changedStateList.size)
        assertEquals(
            EditorViewState(editModel = null, categories = listOf()),
            stateCommunicator.changedStateList[0],
        )
        assertEquals(
            EditorViewState(
                editModel = fakeEditModel,
                categories = listOf(fakeCategories),
            ),
            stateCommunicator.changedStateList[1],
        )
        assertEquals(
            EditorViewState(
                editModel = fakeEditModel.copy(
                    timeRanges = TimeRange(date.shiftMinutes(10), date.shiftMinutes(20)),
                    duration = duration(date.shiftMinutes(10), date.shiftMinutes(20)),
                ),
                categories = listOf(fakeCategories),
            ),
            stateCommunicator.changedStateList[2],
        )
    }

    @Test
    fun test_set_template() {
        val date = Calendar.getInstance().time
        val fakeEditModel = EditModelUi(
            key = 100L,
            date = date,
            timeRanges = TimeRange(from = date.startThisDay(), to = date.endThisDay()),
        )
        val fakeCategories = Categories(
            MainCategory.absent(),
            listOf(SubCategory.absentSubCategory(MainCategory.absent())),
        )
        stateCommunicator.changedStateList.add(
            EditorViewState(editModel = fakeEditModel, categories = listOf(fakeCategories)),
        )

        screenModel.dispatchEvent(EditorEvent.ChangeIsTemplate)

        assertEquals(1, templatesInteractor.addedTemplatesCount)

        assertEquals(0, effectCommunicator.pushEffectList.size)
        assertEquals(0, effectCommunicator.updateEffectCount)

        assertEquals(1, stateCommunicator.updateStateCount)
        assertEquals(3, stateCommunicator.changedStateList.size)
        assertEquals(
            EditorViewState(editModel = null, categories = listOf()),
            stateCommunicator.changedStateList[0],
        )
        assertEquals(
            EditorViewState(
                editModel = fakeEditModel.copy(templateId = null),
                categories = listOf(fakeCategories),
            ),
            stateCommunicator.changedStateList[1],
        )
        assertEquals(
            EditorViewState(
                editModel = fakeEditModel.copy(templateId = 0),
                categories = listOf(fakeCategories),
            ),
            stateCommunicator.changedStateList[2],
        )
    }

    @Test
    fun test_delete_template() {
        val date = Calendar.getInstance().time
        val fakeTemplate = Template(
            templateId = 0,
            startTime = date.startThisDay(),
            endTime = date.endThisDay(),
            category = MainCategory.absent(),
        )
        val fakeEditModel = EditModelUi(
            key = 100L,
            date = date,
            timeRanges = TimeRange(from = date.startThisDay(), to = date.endThisDay()),
            templateId = 0,
        )
        val fakeCategories = Categories(
            MainCategory.absent(),
            listOf(SubCategory.absentSubCategory(MainCategory.absent())),
        )
        stateCommunicator.changedStateList.add(
            EditorViewState(editModel = fakeEditModel, categories = listOf(fakeCategories)),
        )
        templatesInteractor.templatesList.add(fakeTemplate)

        screenModel.dispatchEvent(EditorEvent.ChangeIsTemplate)

        assertEquals(1, templatesInteractor.deleteTemplatesCount)

        assertEquals(0, effectCommunicator.pushEffectList.size)
        assertEquals(0, effectCommunicator.updateEffectCount)

        assertEquals(1, stateCommunicator.updateStateCount)
        assertEquals(3, stateCommunicator.changedStateList.size)
        assertEquals(
            EditorViewState(editModel = null, categories = listOf()),
            stateCommunicator.changedStateList[0],
        )
        assertEquals(
            EditorViewState(
                editModel = fakeEditModel.copy(templateId = 0),
                categories = listOf(fakeCategories),
            ),
            stateCommunicator.changedStateList[1],
        )
        assertEquals(
            EditorViewState(
                editModel = fakeEditModel.copy(templateId = null),
                categories = listOf(fakeCategories),
            ),
            stateCommunicator.changedStateList[2],
        )
    }

    @Test
    fun test_change_parameters_and_categories() {
        val date = Calendar.getInstance().time
        val fakeMainCategory = MainCategory(id = 1, name = "Work")
        val fakeCategories = listOf(
            Categories(
                MainCategory.absent(),
                listOf(SubCategory.absentSubCategory(MainCategory.absent())),
            ),
            Categories(fakeMainCategory, listOf()),
        )
        val fakeEditModel = EditModelUi(
            key = 100L,
            date = date,
            timeRanges = TimeRange(from = date.startThisDay(), to = date.endThisDay()),
            mainCategory = MainCategory.absent(),
            subCategory = SubCategory.absentSubCategory(MainCategory.absent()),
            parameters = EditParameters(isEnableNotification = false),
        )
        stateCommunicator.changedStateList.add(
            EditorViewState(editModel = fakeEditModel, categories = fakeCategories),
        )

        screenModel.dispatchEvent(EditorEvent.ChangeCategories(fakeMainCategory, null))
        screenModel.dispatchEvent(EditorEvent.ChangeParameters(EditParameters(isEnableNotification = true)))

        assertEquals(0, effectCommunicator.pushEffectList.size)
        assertEquals(0, effectCommunicator.updateEffectCount)

        assertEquals(2, stateCommunicator.updateStateCount)
        assertEquals(4, stateCommunicator.changedStateList.size)
        assertEquals(
            EditorViewState(editModel = null, categories = listOf()),
            stateCommunicator.changedStateList[0],
        )
        assertEquals(
            EditorViewState(
                editModel = fakeEditModel,
                categories = fakeCategories,
            ),
            stateCommunicator.changedStateList[1],
        )
        assertEquals(
            EditorViewState(
                editModel = fakeEditModel.copy(
                    mainCategory = fakeMainCategory,
                    subCategory = null,
                ),
                categories = fakeCategories,
            ),
            stateCommunicator.changedStateList[2],
        )
        assertEquals(
            EditorViewState(
                editModel = fakeEditModel.copy(
                    mainCategory = fakeMainCategory,
                    subCategory = null,
                    parameters = fakeEditModel.parameters.copy(isEnableNotification = true),
                ),
                categories = fakeCategories,
            ),
            stateCommunicator.changedStateList[3],
        )
    }

    @Test
    fun test_press_back_button_event() = runBlocking {
        screenModel.dispatchEvent(EditorEvent.PressBackButton)

        assertEquals(true, navigationManager.isNavigateToPrevious)

        assertEquals(0, editorInteractor.fetchedModelCount)
        assertEquals(0, categoriesInteractor.fetchedCategoriesCount)
        assertEquals(0, timeTaskInteractor.updateTaskCount)

        assertEquals(2, stateCommunicator.changedStateList.size)
        assertEquals(1, stateCommunicator.updateStateCount)

        assertEquals(0, effectCommunicator.pushEffectList.size)
        assertEquals(0, effectCommunicator.updateEffectCount)
    }
}

private class FakeTimeTaskInteractor : TimeTaskInteractor {

    val timeTasksList = mutableListOf<TimeTask>()

    var addedTaskCount = 0
    var updateTaskCount = 0
    var deleteTaskCount = 0

    var errorWhileAction = false

    override suspend fun addTimeTask(timeTask: TimeTask): Either<EditorFailures, Unit> {
        addedTaskCount++
        return if (!errorWhileAction) {
            timeTasksList.add(timeTask)
            Either.Right(Unit)
        } else {
            Either.Left(EditorFailures.OtherError(SQLiteException()))
        }
    }

    override suspend fun updateTimeTask(timeTask: TimeTask): Either<EditorFailures, Unit> {
        updateTaskCount++
        return if (!errorWhileAction) {
            val index = timeTasksList.indexOfFirst { it.key == timeTask.key }
            timeTasksList[index] = timeTask
            Either.Right(Unit)
        } else {
            Either.Left(EditorFailures.TimeOverlayError(null, null))
        }
    }

    override suspend fun deleteTimeTask(key: Long): Either<EditorFailures, Unit> {
        deleteTaskCount++
        return if (!errorWhileAction) {
            timeTasksList.removeAt(timeTasksList.indexOfFirst { it.key == key })
            Either.Right(Unit)
        } else {
            Either.Left(EditorFailures.OtherError(SQLiteException()))
        }
    }
}

private class FakeEditorInteractor : EditorInteractor {

    var editModel: EditModel? = null

    var fetchedModelCount = 0
    var saveModelCount = 0

    override fun fetchEditModel(): EditModel {
        fetchedModelCount++
        return editModel ?: throw NullPointerException()
    }

    override fun sendEditModel(model: EditModel) {
        saveModelCount++
        editModel = model
    }
}

private class FakeCategoriesInteractor : CategoriesInteractor {

    val categoriesList = mutableListOf<Categories>()

    var fetchedCategoriesCount = 0

    var errorWhileAction = false

    override suspend fun fetchCategories(): DomainResult<EditorFailures, List<Categories>> {
        fetchedCategoriesCount++
        return if (!errorWhileAction) {
            Either.Right(categoriesList)
        } else {
            Either.Left(EditorFailures.OtherError(SQLiteException()))
        }
    }
}

private class FakeTemplatesInteractor : TemplatesInteractor {

    val templatesList = mutableListOf<Template>()

    var addedTemplatesCount = 0
    var fetchTemplatesCount = 0
    var updateTemplatesCount = 0
    var deleteTemplatesCount = 0

    var errorWhileAction = false

    override suspend fun fetchTemplates(): DomainResult<EditorFailures, List<Template>> {
        fetchTemplatesCount++
        return if (!errorWhileAction) {
            Either.Right(templatesList)
        } else {
            Either.Left(EditorFailures.OtherError(SQLiteException()))
        }
    }

    override suspend fun updateTemplate(template: Template): DomainResult<EditorFailures, Unit> {
        updateTemplatesCount++
        return if (!errorWhileAction) {
            val index = templatesList.indexOfFirst { it.templateId == template.templateId }
            templatesList[index] = template
            Either.Right(Unit)
        } else {
            Either.Left(EditorFailures.OtherError(SQLiteException()))
        }
    }

    override suspend fun addTemplate(template: Template): DomainResult<EditorFailures, Int> {
        addedTemplatesCount++
        return if (!errorWhileAction) {
            val templateId = templatesList.lastOrNull()?.templateId?.inc() ?: 0
            templatesList.add(template.copy(templateId = templateId))
            Either.Right(templateId)
        } else {
            Either.Left(EditorFailures.OtherError(SQLiteException()))
        }
    }

    override suspend fun deleteTemplateById(id: Int): DomainResult<EditorFailures, Unit> {
        deleteTemplatesCount++
        return if (!errorWhileAction) {
            templatesList.removeAt(templatesList.indexOfFirst { it.templateId == id })
            Either.Right(Unit)
        } else {
            Either.Left(EditorFailures.OtherError(SQLiteException()))
        }
    }
}

private class FakeTimeTaskAlarmManager : TimeTaskAlarmManager {

    val notificationTasks = mutableListOf<TimeTask>()

    var addedNotificationCount = 0
    var updateNotificationCount = 0
    var deleteNotificationCount = 0

    override fun addNotifyAlarm(timeTask: TimeTask) {
        addedNotificationCount++
        notificationTasks.add(timeTask)
    }

    override fun updateNotifyAlarm(timeTask: TimeTask) {
        updateNotificationCount++
        val index = notificationTasks.indexOfFirst { it.key == timeTask.key }
        notificationTasks[index] = timeTask
    }

    override fun deleteNotifyAlarm(timeTask: TimeTask) {
        deleteNotificationCount++
        val index = notificationTasks.indexOfFirst { it.key == timeTask.key }
        notificationTasks.removeAt(index)
    }
}

private class FakeNavigationManager : NavigationManager {

    var isNavigateToHome = false
    var isNavigateToTemplates = false
    var isNavigateToCategories = false
    var isNavigateToPrevious = false

    override fun navigateToHomeScreen() {
        isNavigateToHome = true
    }

    override fun navigateToTemplatesScreen() {
        isNavigateToTemplates = true
    }

    override fun navigateToCategoriesScreen() {
        isNavigateToCategories = true
    }

    override fun navigateToPreviousFeature() {
        isNavigateToPrevious = true
    }
}

private class FakeDateManager : DateManager {

    var currentDate: Date = Calendar.getInstance().time

    override fun fetchCurrentDate() = currentDate

    override fun fetchBeginningCurrentDay() = currentDate.startThisDay()

    override fun fetchEndCurrentDay() = currentDate.endThisDay()

    override fun calculateLeftTime(endTime: Date) = endTime.time - currentDate.time

    override fun calculateProgress(startTime: Date, endTime: Date): Float {
        val currentTime = fetchCurrentDate().time
        val pastTime = ((currentTime - startTime.time).toMinutes()).toFloat()
        val duration = ((endTime.time - startTime.time).toMinutes()).toFloat()
        val progress = pastTime / duration

        return if (progress < 0f) 0f else if (progress > 1f) 1f else progress
    }
}

private class FakeEditorStateCommunicator : EditorStateCommunicator {

    var changedStateList = mutableListOf(EditorViewState())
    var updateStateCount = 0

    override suspend fun read(): EditorViewState {
        return changedStateList.last()
    }

    override fun update(data: EditorViewState) {
        updateStateCount++
        changedStateList.add(data)
    }

    override suspend fun collect(collector: FlowCollector<EditorViewState>) = Unit
}

private class FakeEditorEffectCommunicator : EditorEffectCommunicator {

    var pushEffectList = mutableListOf<EditorEffect>()
    var updateEffectCount = 0

    override suspend fun read(): EditorEffect {
        return pushEffectList.last()
    }

    override fun update(data: EditorEffect) {
        updateEffectCount++
        pushEffectList.add(data)
    }

    override suspend fun collect(collector: FlowCollector<EditorEffect>) = Unit
}

private class TestCoroutineManager : CoroutineManager.Abstract(
    uiDispatcher = TestCoroutineDispatcher(),
    backgroundDispatcher = TestCoroutineDispatcher(),
)

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
package ru.aleshin.features.editor.impl.ui

import android.database.sqlite.SQLiteException
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.Categories
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.categories.SubCategory
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.domain.entities.schedules.UndefinedTask
import ru.aleshin.core.domain.entities.template.Template
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
import ru.aleshin.core.ui.notifications.TimeTaskAlarmManager
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.editor.impl.domain.common.convertToTimeTask
import ru.aleshin.features.editor.impl.domain.entites.EditModel
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.EditorInteractor
import ru.aleshin.features.editor.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.TimeTaskInteractor
import ru.aleshin.features.editor.impl.domain.interactors.UndefinedTasksInteractor
import ru.aleshin.features.editor.impl.navigation.NavigationManager
import ru.aleshin.features.editor.impl.presentation.mappers.mapToDomain
import ru.aleshin.features.editor.impl.presentation.mappers.mapToUi
import ru.aleshin.features.editor.impl.presentation.models.categories.CategoriesUi
import ru.aleshin.features.editor.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditModelUi
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditParameters
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
    private lateinit var undefinedTasksInteractor: UndefinedTasksInteractor
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
                undefinedTasksInteractor = undefinedTasksInteractor,
                timeTaskAlarmManager = timeTaskAlarmManager,
                navigationManager = navigationManager,
            ),
            editorWorkProcessor = EditorWorkProcessor.Base(
                editorInteractor = editorInteractor,
                categoriesInteractor = categoriesInteractor,
                templatesInteractor = templatesInteractor,
            ),
            navigationManager = navigationManager,
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
        undefinedTasksInteractor = FakeUndefinedTaskInteractor()
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
        val fakeCategories = Categories(MainCategory(), listOf(SubCategory()))
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
                categories = listOf(fakeCategories).map { it.mapToUi() },
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
        val fakeCategories = Categories(MainCategory(), listOf(SubCategory()))
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
        val initEditModel = EditModel(
            key = 100L,
            date = date,
            startTime = date.shiftMinutes(10),
            endTime = date.shiftMinutes(20),
            mainCategory = MainCategory(id = 1, customName = "Work"),
        )
        val fakeEditModel = EditModelUi(
            key = 100L,
            date = date,
            timeRange = TimeRange(from = date.shiftMinutes(20), to = date.endThisDay()),
            duration = duration(date.shiftMinutes(20), date.endThisDay()),
            mainCategory = MainCategoryUi(id = 1, customName = "Work"),
        )
        val fakeCategories = CategoriesUi(MainCategoryUi(id = 1, customName = "Work"), listOf())
        dateManager.currentDate = date.startThisDay()
        timeTaskInteractor.timeTasksList.add(initEditModel.convertToTimeTask())
        timeTaskAlarmManager.notificationTasks.add(initEditModel.convertToTimeTask())
        stateCommunicator.changedStateList.add(
            EditorViewState(editModel = fakeEditModel, categories = listOf(fakeCategories)),
        )

        screenModel.dispatchEvent(EditorEvent.PressSaveButton)

        assertEquals(true, navigationManager.isNavigateToHome)

        assertEquals(1, timeTaskAlarmManager.addedOrUpdatedNotificationCount)
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
        val fakeEditModel = EditModelUi(
            key = 0L,
            date = date,
            timeRange = TimeRange(from = date.shiftMinutes(20), to = date.endThisDay()),
            duration = duration(date.shiftMinutes(20), date.endThisDay()),
            mainCategory = MainCategoryUi(id = 1, customName = "Work"),
        )
        val fakeCategories = CategoriesUi(MainCategoryUi(id = 1, customName = "Work"), listOf())
        dateManager.currentDate = date.startThisDay()
        stateCommunicator.changedStateList.add(
            EditorViewState(editModel = fakeEditModel, categories = listOf(fakeCategories)),
        )

        screenModel.dispatchEvent(EditorEvent.PressSaveButton)

        assertEquals(true, navigationManager.isNavigateToHome)

        assertEquals(1, timeTaskAlarmManager.addedOrUpdatedNotificationCount)
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
            timeRange = TimeRange(from = date.startThisDay(), to = date.endThisDay()),
        )
        val fakeCategories = CategoriesUi(MainCategoryUi(), listOf(SubCategoryUi()))
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
                    timeRange = TimeRange(date.shiftMinutes(10), date.shiftMinutes(20)),
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
            timeRange = TimeRange(from = date.startThisDay(), to = date.endThisDay()),
        )
        val fakeCategories = CategoriesUi(MainCategoryUi(), listOf(SubCategoryUi()))
        stateCommunicator.changedStateList.add(
            EditorViewState(editModel = fakeEditModel, categories = listOf(fakeCategories)),
        )

        screenModel.dispatchEvent(EditorEvent.CreateTemplate)

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
    fun test_change_parameters_and_categories() {
        val date = Calendar.getInstance().time
        val fakeMainCategory = MainCategoryUi(id = 1, customName = "Work")
        val fakeCategories = listOf(
            CategoriesUi(MainCategoryUi(), listOf(SubCategoryUi())),
            CategoriesUi(fakeMainCategory, listOf()),
        )
        val fakeEditModel = EditModelUi(
            key = 100L,
            date = date,
            timeRange = TimeRange(from = date.startThisDay(), to = date.endThisDay()),
            mainCategory = MainCategoryUi(),
            subCategory = SubCategoryUi(),
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
    fun test_press_back_button_event() {
        screenModel.dispatchEvent(EditorEvent.PressBackButton)

        assertEquals(true, navigationManager.isNavigateToPrevious)

        assertEquals(0, editorInteractor.fetchedModelCount)
        assertEquals(0, categoriesInteractor.fetchedCategoriesCount)
        assertEquals(0, timeTaskInteractor.updateTaskCount)

        assertEquals(1, stateCommunicator.changedStateList.size)
        assertEquals(0, stateCommunicator.updateStateCount)

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

    override suspend fun addTimeTask(timeTask: TimeTask): Either<EditorFailures, Long> {
        addedTaskCount++
        return if (!errorWhileAction) {
            timeTasksList.add(timeTask)
            Either.Right(timeTask.key)
        } else {
            Either.Left(EditorFailures.OtherError(SQLiteException()))
        }
    }

    override suspend fun updateTimeTask(timeTask: TimeTask): Either<EditorFailures, Long> {
        updateTaskCount++
        return if (!errorWhileAction) {
            val index = timeTasksList.indexOfFirst { it.key == timeTask.key }
            timeTasksList[index] = timeTask
            Either.Right(timeTask.key)
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
    var addedCategoriesCount = 0

    var errorWhileAction = false

    override suspend fun fetchCategories(): DomainResult<EditorFailures, List<Categories>> {
        fetchedCategoriesCount++
        return if (!errorWhileAction) {
            Either.Right(categoriesList)
        } else {
            Either.Left(EditorFailures.OtherError(SQLiteException()))
        }
    }

    override suspend fun addSubCategory(subCategory: SubCategory): DomainResult<EditorFailures, Unit> {
        addedCategoriesCount++
        return if (!errorWhileAction) {
            val model = categoriesList.find { it.category == subCategory.mainCategory }
            if (model != null) {
                categoriesList[categoriesList.indexOf(model)] = model.copy(
                    subCategories = model.subCategories.toMutableList().apply { add(subCategory) }
                )
            } else {
                categoriesList.add(Categories(subCategory.mainCategory, listOf(subCategory)))
            }
            Either.Right(Unit)
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

private class FakeUndefinedTaskInteractor : UndefinedTasksInteractor {

    private val undefinedTasksList = mutableListOf<UndefinedTask>()

    var fetchedTasksCount = 0
    var deletedTasksCount = 0

    var errorWhileAction = false
    override suspend fun fetchAllUndefinedTasks(): DomainResult<EditorFailures, List<UndefinedTask>> {
        fetchedTasksCount++
        return if (!errorWhileAction) {
            Either.Right(undefinedTasksList)
        } else {
            Either.Left(EditorFailures.OtherError(SQLiteException()))
        }
    }

    override suspend fun deleteUndefinedTask(taskId: Long): UnitDomainResult<EditorFailures> {
        deletedTasksCount++
        return if (!errorWhileAction) {
            undefinedTasksList.removeAt(undefinedTasksList.indexOfFirst { it.id == taskId })
            Either.Right(Unit)
        } else {
            Either.Left(EditorFailures.OtherError(SQLiteException()))
        }
    }
}

private class FakeTimeTaskAlarmManager : TimeTaskAlarmManager {

    val notificationTasks = mutableListOf<TimeTask>()

    var addedOrUpdatedNotificationCount = 0
    var deleteNotificationCount = 0

    override fun addOrUpdateNotifyAlarm(timeTask: TimeTask) {
        val index = notificationTasks.indexOfFirst { it.key == timeTask.key }
        if (index != -1) notificationTasks[index] = timeTask else notificationTasks.add(timeTask)
        addedOrUpdatedNotificationCount++
    }

    override fun deleteNotifyAlarm(timeTask: TimeTask) {
        deleteNotificationCount++
        val index = notificationTasks.indexOfFirst { it.key == timeTask.key }.takeIf { it != -1 } ?: return
        notificationTasks.removeAt(index)
    }
}

private class FakeNavigationManager : NavigationManager {

    var isNavigateToHome = false
    var isNavigateToTemplates = false
    var isNavigateToPrevious = false

    override fun navigateToHomeScreen() {
        isNavigateToHome = true
    }

    override fun navigateToTemplatesScreen() {
        isNavigateToTemplates = true
    }

    override fun navigateToBack() {
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

    override fun setCurrentHMS(date: Date): Date {
        val currentCalendar = Calendar.getInstance().apply {
            time = currentDate
        }
        val targetCalendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
            set(Calendar.SECOND, currentCalendar.get(Calendar.SECOND))
            set(Calendar.MILLISECOND, currentCalendar.get(Calendar.MILLISECOND))
        }
        return targetCalendar.time
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
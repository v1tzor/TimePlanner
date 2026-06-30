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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.OutputResult
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.editor.api.EditorFeatureComponent.EditorOutput
import ru.aleshin.features.editor.impl.presentation.models.categories.CategoriesUi
import ru.aleshin.features.editor.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.features.editor.impl.presentation.models.template.TemplateUi
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorAction
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEffect
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEvent
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorInput
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorState
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.EditorWorkCommand
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.EditorWorkProcessor
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.TimeTaskWorkCommand
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.TimeTaskWorkProcessor
import ru.aleshin.features.editor.impl.presentation.ui.editor.store.CategoryValidator
import ru.aleshin.features.editor.impl.presentation.ui.editor.store.EditorComposeStore
import ru.aleshin.features.editor.impl.presentation.ui.editor.store.TimeRangeValidator
import java.util.Date

/**
 * @author Stanislav Aleshin on 01.06.2023.
 */
internal class EditorScreenModelTest {

    private lateinit var store: EditorComposeStore
    private lateinit var editorWorkProcessor: FakeEditorWorkProcessor
    private lateinit var timeTaskWorkProcessor: FakeTimeTaskWorkProcessor
    private lateinit var outputs: MutableList<EditorOutput>

    private val date = Date(1_700_000_000_000L)
    private val timeRange = TimeRange(Date(date.time), Date(date.time + 60_000L))
    private val mainCategory = MainCategory(id = 1, customName = "Work")
    private val mainCategoryUi = MainCategoryUi(id = 1, customName = "Work")
    private val categories = listOf(CategoriesUi(mainCategoryUi, emptyList()))
    private val undefinedTasks = listOf(UndefinedTaskUi(id = 42L, mainCategory = mainCategoryUi))

    @Before
    fun setup() {
        outputs = mutableListOf()
        editorWorkProcessor = FakeEditorWorkProcessor(categories = categories)
        timeTaskWorkProcessor = FakeTimeTaskWorkProcessor(undefinedTasks = undefinedTasks)
    }

    @After
    fun tearDown() {
        if (::store.isInitialized) store.onDestroy()
    }

    @Test
    fun initialize_sets_edit_model_and_loads_related_data() = runTest {
        store = createStore(StandardTestDispatcher(testScheduler))
        store.setOutputConsumer(OutputConsumer { outputs.add(it) })

        store.initialize(createInput(), isRestore = false)
        testScheduler.advanceUntilIdle()

        assertEquals(1L, store.state.editModel?.key)
        assertEquals(mainCategoryUi, store.state.editModel?.mainCategory)
        assertEquals(categories, store.state.categories)
        assertEquals(emptyList<TemplateUi>(), store.state.templates)
        assertEquals(undefinedTasks, store.state.undefinedTasks)
        assertEquals(emptyList<EditorOutput>(), outputs)
    }

    @Test
    fun press_back_button_emits_navigate_to_back_output() = runTest {
        store = createStore(StandardTestDispatcher(testScheduler))
        store.setOutputConsumer(OutputConsumer { outputs.add(it) })

        store.dispatchEvent(EditorEvent.PressBackButton)
        testScheduler.advanceUntilIdle()

        assertEquals(listOf(EditorOutput.NavigateToBack), outputs)
    }

    @Test
    fun press_save_button_with_valid_model_emits_navigate_to_back_output() = runTest {
        store = createStore(StandardTestDispatcher(testScheduler))
        store.setOutputConsumer(OutputConsumer { outputs.add(it) })
        store.initialize(createInput(), isRestore = false)
        testScheduler.advanceUntilIdle()

        store.dispatchEvent(EditorEvent.PressSaveButton)
        testScheduler.advanceUntilIdle()

        assertEquals(1, timeTaskWorkProcessor.saveCount)
        assertEquals(listOf(EditorOutput.NavigateToBack), outputs)
    }

    private fun createStore(dispatcher: CoroutineDispatcher): EditorComposeStore {
        return EditorComposeStore(
            timeTaskWorkProcessor = timeTaskWorkProcessor,
            editorWorkProcessor = editorWorkProcessor,
            timeRangeValidator = TimeRangeValidator.Base(),
            categoryValidator = CategoryValidator.Base(),
            stateCommunicator = StateCommunicator.Default(EditorState()),
            effectCommunicator = EffectCommunicator.Default(),
            coroutineManager = TestCoroutineManager(dispatcher),
        )
    }

    private fun createInput() = EditorInput(
        timeTask = TimeTask(
            key = 1L,
            date = date,
            createdAt = date,
            timeRange = timeRange,
            category = mainCategory,
        ),
        template = null,
        undefinedTaskId = null,
    )

    private class FakeEditorWorkProcessor(
        private val categories: List<CategoriesUi>,
    ) : EditorWorkProcessor {

        override suspend fun work(command: EditorWorkCommand) = when (command) {
            is EditorWorkCommand.SetupEditModel -> ActionResult(
                EditorAction.SetUp(command.editModel, categories)
            )
            is EditorWorkCommand.LoadTemplates -> ActionResult(
                EditorAction.UpdateTemplates(emptyList())
            )
            is EditorWorkCommand.AddSubCategory -> ActionResult(
                EditorAction.UpdateCategories(categories)
            )
            is EditorWorkCommand.AddTemplate -> ActionResult(
                EditorAction.UpdateTemplateId(1)
            )
            is EditorWorkCommand.ApplyTemplate -> ActionResult(
                EditorAction.UpdateEditModel(command.model)
            )
            is EditorWorkCommand.ApplyUndefinedTask -> ActionResult(
                EditorAction.UpdateEditModel(command.model)
            )
        }
    }

    private class FakeTimeTaskWorkProcessor(
        private val undefinedTasks: List<UndefinedTaskUi>,
    ) : TimeTaskWorkProcessor {

        var saveCount = 0

        override suspend fun work(command: TimeTaskWorkCommand) = when (command) {
            is TimeTaskWorkCommand.LoadUndefinedTasks -> ActionResult(
                EditorAction.UpdateUndefinedTasks(undefinedTasks)
            )
            is TimeTaskWorkCommand.AddOrSaveModel -> {
                saveCount++
                OutputResult(EditorOutput.NavigateToBack)
            }
            is TimeTaskWorkCommand.DeleteModel -> OutputResult(EditorOutput.NavigateToBack)
        }
    }

    private class TestCoroutineManager(
        dispatcher: CoroutineDispatcher,
    ) : CoroutineManager.Abstract(
        defaultDispatcher = dispatcher,
        ioDispatcher = dispatcher,
        uiDispatcher = dispatcher,
    )
}

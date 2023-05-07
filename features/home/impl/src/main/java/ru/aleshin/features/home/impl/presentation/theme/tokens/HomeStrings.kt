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
package ru.aleshin.features.home.impl.presentation.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.core.ui.theme.tokens.TimePlannerLanguage

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
internal data class HomeStrings(
    val topAppBarHomeTitle: String,
    val topAppBarCategoriesTitle: String,
    val mainCategoryTitle: String,
    val topAppBarCalendarIconDesc: String,
    val topAppBarMenuIconDesc: String,
    val nextDateIconDesc: String,
    val previousDateIconDesc: String,
    val dateDialogPickerHeadline: String,
    val dateDialogPickerTitle: String,
    val timeTaskExpandedIconDesc: String,
    val timeTaskCheckIconDesc: String,
    val timeTaskMoreIconDesc: String,
    val timeTaskAddIconDesc: String,
    val timeTaskRemoveIconDesc: String,
    val timeTaskIncreaseTimeTitle: String,
    val timeTaskReduceTimeTitle: String,
    val startTimeTaskTitlePlaceHolder: String,
    val addTimeTaskIconsDesc: String,
    val addTimeTaskTitle: String,
    val topAppBarBackIconDesc: String,
    val topAppBarMoreIconDesc: String,
    val topAppBarTemplatesTitle: String,
    val mainCategoryChooserTitle: String,
    val subCategoryChooserTitle: String,
    val mainCategoryChooserExpandedIconDesc: String,
    val categoryNotSelectedTitle: String,
    val subCategoryDialogAddTitle: String,
    val subCategoryDialogMainCategoryFormat: String,
    val timeFieldStartLabel: String,
    val timeFieldEndLabel: String,
    val parameterChooserSwitchIconDesc: String,
    val timePickerHeader: String,
    val timePickerSeparator: String,
    val notifyParameterTitle: String,
    val notifyParameterDesc: String,
    val statisticsParameterTitle: String,
    val statisticsParameterDesc: String,
    val saveTaskButtonTitle: String,
    val cancelButtonTitle: String,
    val templateIconDesc: String,
    val emptyScheduleTitle: String,
    val createScheduleTitle: String,
    val createScheduleDesc: String,
    val otherError: String,
    val shiftError: String,
    val addSubCategoryTitle: String,
    val subCategoryFieldLabel: String,
    val dialogCreateTitle: String,
    val emptySubCategoriesTitle: String,
    val updateCategoryTitle: String,
    val deleteCategoryTitle: String,
    val subCategoryTitle: String,
    val warningDeleteCategoryText: String,
)

internal val russianHomeString = HomeStrings(
    topAppBarHomeTitle = "Главная",
    topAppBarCategoriesTitle = "Категории",
    topAppBarCalendarIconDesc = "Выбрать дату",
    topAppBarMenuIconDesc = "Открыть меню",
    mainCategoryTitle = "Основная",
    nextDateIconDesc = "Следующая дата",
    previousDateIconDesc = "Предыдущая дата",
    dateDialogPickerTitle = "Дневной план",
    dateDialogPickerHeadline = "Выберите дату",
    timeTaskExpandedIconDesc = "Дополнительно",
    timeTaskCheckIconDesc = "Выполнено",
    timeTaskMoreIconDesc = "Редактировать",
    timeTaskAddIconDesc = "Добавить время",
    timeTaskRemoveIconDesc = "Убавить время",
    timeTaskIncreaseTimeTitle = "Увеличить",
    timeTaskReduceTimeTitle = "Убавить",
    startTimeTaskTitlePlaceHolder = "00:00",
    addTimeTaskIconsDesc = "Добавить задачу",
    addTimeTaskTitle = "Свободное время",
    topAppBarBackIconDesc = "Назад",
    topAppBarMoreIconDesc = "Доплнительно",
    topAppBarTemplatesTitle = "Шаблоны",
    mainCategoryChooserTitle = "Категория",
    subCategoryChooserTitle = "Подкатегория",
    mainCategoryChooserExpandedIconDesc = "Выбрать категорию",
    categoryNotSelectedTitle = "Отсутвует",
    subCategoryDialogAddTitle = "Добавить",
    subCategoryDialogMainCategoryFormat = "Категория: %s",
    timeFieldStartLabel = "Старт",
    timeFieldEndLabel = "Конец",
    parameterChooserSwitchIconDesc = "Установить параметр",
    timePickerHeader = "Выберите время",
    timePickerSeparator = ":",
    notifyParameterTitle = "Уведомления",
    notifyParameterDesc = "Отправить уведомление при выполнении задачи",
    statisticsParameterTitle = "Статистика",
    statisticsParameterDesc = "Учитывать данные в статистике",
    saveTaskButtonTitle = "Сохранить",
    cancelButtonTitle = "Отменить",
    templateIconDesc = "Добавить в шаблоны",
    emptyScheduleTitle = "План отсутствует",
    createScheduleTitle = "Создать",
    createScheduleDesc = "Создать план для выбранного дня",
    otherError = "Ошибка! Обратитесь к разработчику.",
    shiftError = "Сдвиг невозможен!",
    addSubCategoryTitle = "Добавить подкатегорию",
    subCategoryFieldLabel = "Название",
    dialogCreateTitle = "Создать",
    emptySubCategoriesTitle = "Список пуст",
    updateCategoryTitle = "Редактировать",
    deleteCategoryTitle = "Удалить",
    subCategoryTitle = "Подкатегории",
    warningDeleteCategoryText = "Вы уверены что хотите удалить основную категорию? " +
        "Данное действие приведёт к уничтожению всех ранее запланированных задач.",
)

internal val englishHomeString = HomeStrings(
    topAppBarHomeTitle = "Home",
    topAppBarCategoriesTitle = "Categories",
    mainCategoryTitle = "Main",
    topAppBarCalendarIconDesc = "Select a date",
    topAppBarMenuIconDesc = "Open menu",
    nextDateIconDesc = "Next date",
    previousDateIconDesc = "Previous date",
    dateDialogPickerTitle = "Daily plan",
    dateDialogPickerHeadline = "Select a date",
    timeTaskExpandedIconDesc = "More",
    timeTaskCheckIconDesc = "Completed",
    timeTaskMoreIconDesc = "Edit",
    timeTaskAddIconDesc = "Add time",
    timeTaskRemoveIconDesc = "Reduce the time",
    timeTaskIncreaseTimeTitle = "Increase",
    timeTaskReduceTimeTitle = "Reduce",
    startTimeTaskTitlePlaceHolder = "00:00",
    addTimeTaskIconsDesc = "Add task",
    addTimeTaskTitle = "Free time",
    topAppBarBackIconDesc = "Back",
    topAppBarMoreIconDesc = "More",
    topAppBarTemplatesTitle = "Templates",
    mainCategoryChooserTitle = "Category",
    mainCategoryChooserExpandedIconDesc = "Select category",
    subCategoryChooserTitle = "Subcategory",
    categoryNotSelectedTitle = "Absent",
    subCategoryDialogAddTitle = "Add",
    subCategoryDialogMainCategoryFormat = "Category: %s",
    timeFieldStartLabel = "Start",
    timeFieldEndLabel = "End",
    parameterChooserSwitchIconDesc = "Set parameter",
    timePickerHeader = "Enter time",
    timePickerSeparator = ":",
    notifyParameterTitle = "Notifications",
    notifyParameterDesc = "Send a notification when completing a task",
    statisticsParameterTitle = "Statistics",
    statisticsParameterDesc = "Take into account data in statistics",
    saveTaskButtonTitle = "Save",
    cancelButtonTitle = "Cancel",
    templateIconDesc = "Add to Templates",
    emptyScheduleTitle = "There is no plan",
    createScheduleTitle = "Create",
    createScheduleDesc = "Create a plan for the selected day",
    otherError = "Error! Contact the developer.",
    shiftError = "The shift is not possible!",
    addSubCategoryTitle = "Add subcategory",
    subCategoryFieldLabel = "Name",
    dialogCreateTitle = "Create",
    emptySubCategoriesTitle = "List is empty",
    updateCategoryTitle = "Edit",
    deleteCategoryTitle = "Remove",
    subCategoryTitle = "Subcategories",
    warningDeleteCategoryText = "Are you sure you want to delete the main category? " +
        "This action will destroy all previously scheduled tasks.",
)

internal val LocalHomeStrings = staticCompositionLocalOf<HomeStrings> {
    error("Home Strings is not provided")
}

internal fun fetchHomeStrings(language: TimePlannerLanguage) = when (language) {
    TimePlannerLanguage.EN -> englishHomeString
    TimePlannerLanguage.RU -> russianHomeString
}

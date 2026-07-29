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
package ru.aleshin.features.editor.impl.presentation.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerLanguage

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal data class EditorGoalStrings(
    val createGoalTitle: String,
    val editGoalTitle: String,
    val titleLabel: String,
    val titlePlaceholder: String,
    val scopeTitle: String,
    val allTasksTitle: String,
    val categoryScopeTitle: String,
    val subCategoryScopeTitle: String,
    val chooseCategoryTitle: String,
    val chooseSubCategoryTitle: String,
    val metricTitle: String,
    val durationMetricTitle: String,
    val taskCountMetricTitle: String,
    val directionTitle: String,
    val atLeastTitle: String,
    val atMostTitle: String,
    val targetMinutesLabel: String,
    val targetTasksLabel: String,
    val deadlineTitle: String,
    val saveTitle: String,
    val titleError: String,
    val scopeError: String,
    val targetError: String,
    val deadlineError: String,
)

private val englishGoalStrings = EditorGoalStrings(
    createGoalTitle = "New goal",
    editGoalTitle = "Edit goal",
    titleLabel = "Name",
    titlePlaceholder = "For example, English",
    scopeTitle = "What to count",
    allTasksTitle = "All tasks",
    categoryScopeTitle = "Category",
    subCategoryScopeTitle = "Subcategory",
    chooseCategoryTitle = "Choose category",
    chooseSubCategoryTitle = "Choose subcategory",
    metricTitle = "Metric",
    durationMetricTitle = "Duration",
    taskCountMetricTitle = "Task count",
    directionTitle = "Goal type",
    atLeastTitle = "At least",
    atMostTitle = "No more than",
    targetMinutesLabel = "Target, minutes",
    targetTasksLabel = "Target, tasks",
    deadlineTitle = "Deadline",
    saveTitle = "Save",
    titleError = "Enter a goal name",
    scopeError = "Choose the category to count",
    targetError = "Enter a value greater than zero",
    deadlineError = "Choose a deadline on or after the creation date",
)

private val russianGoalStrings = EditorGoalStrings(
    createGoalTitle = "Новая цель",
    editGoalTitle = "Редактирование цели",
    titleLabel = "Название",
    titlePlaceholder = "Например, Английский",
    scopeTitle = "Что учитывать",
    allTasksTitle = "Все задачи",
    categoryScopeTitle = "Категория",
    subCategoryScopeTitle = "Подкатегория",
    chooseCategoryTitle = "Выберите категорию",
    chooseSubCategoryTitle = "Выберите подкатегорию",
    metricTitle = "Метрика",
    durationMetricTitle = "Длительность",
    taskCountMetricTitle = "Количество задач",
    directionTitle = "Тип цели",
    atLeastTitle = "Не менее",
    atMostTitle = "Не более",
    targetMinutesLabel = "Цель, минут",
    targetTasksLabel = "Цель, задач",
    deadlineTitle = "Дедлайн",
    saveTitle = "Сохранить",
    titleError = "Введите название цели",
    scopeError = "Выберите категорию для учёта",
    targetError = "Введите значение больше нуля",
    deadlineError = "Выберите дедлайн не раньше даты создания",
)

private val germanGoalStrings = englishGoalStrings.copy(
    createGoalTitle = "Neues Ziel",
    editGoalTitle = "Ziel bearbeiten",
    titleLabel = "Name",
    titlePlaceholder = "Zum Beispiel Englisch",
    scopeTitle = "Was soll gezählt werden",
    allTasksTitle = "Alle Aufgaben",
    categoryScopeTitle = "Kategorie",
    subCategoryScopeTitle = "Unterkategorie",
    chooseCategoryTitle = "Kategorie auswählen",
    chooseSubCategoryTitle = "Unterkategorie auswählen",
    metricTitle = "Metrik",
    durationMetricTitle = "Dauer",
    taskCountMetricTitle = "Aufgabenanzahl",
    directionTitle = "Zieltyp",
    atLeastTitle = "Mindestens",
    atMostTitle = "Höchstens",
    targetMinutesLabel = "Ziel, Minuten",
    targetTasksLabel = "Ziel, Aufgaben",
    deadlineTitle = "Frist",
    saveTitle = "Speichern",
    titleError = "Gib einen Zielnamen ein",
    scopeError = "Wähle die zu zählende Kategorie",
    targetError = "Gib einen Wert größer als null ein",
)

private val spanishGoalStrings = englishGoalStrings.copy(
    createGoalTitle = "Nuevo objetivo",
    editGoalTitle = "Editar objetivo",
    titleLabel = "Nombre",
    titlePlaceholder = "Por ejemplo, Inglés",
    scopeTitle = "Qué contar",
    allTasksTitle = "Todas las tareas",
    categoryScopeTitle = "Categoría",
    subCategoryScopeTitle = "Subcategoría",
    chooseCategoryTitle = "Elegir categoría",
    chooseSubCategoryTitle = "Elegir subcategoría",
    metricTitle = "Métrica",
    durationMetricTitle = "Duración",
    taskCountMetricTitle = "Número de tareas",
    directionTitle = "Tipo de objetivo",
    atLeastTitle = "Al menos",
    atMostTitle = "Como máximo",
    targetMinutesLabel = "Objetivo, minutos",
    targetTasksLabel = "Objetivo, tareas",
    deadlineTitle = "Fecha límite",
    saveTitle = "Guardar",
    titleError = "Introduce el nombre del objetivo",
    scopeError = "Elige la categoría que se contará",
    targetError = "Introduce un valor mayor que cero",
)

private val persianGoalStrings = englishGoalStrings.copy(
    createGoalTitle = "هدف جدید",
    editGoalTitle = "ویرایش هدف",
    titleLabel = "نام",
    titlePlaceholder = "برای مثال، انگلیسی",
    scopeTitle = "چه چیزی محاسبه شود",
    allTasksTitle = "همه کارها",
    categoryScopeTitle = "دسته",
    subCategoryScopeTitle = "زیردسته",
    chooseCategoryTitle = "انتخاب دسته",
    chooseSubCategoryTitle = "انتخاب زیردسته",
    metricTitle = "معیار",
    durationMetricTitle = "مدت",
    taskCountMetricTitle = "تعداد کارها",
    directionTitle = "نوع هدف",
    atLeastTitle = "حداقل",
    atMostTitle = "حداکثر",
    targetMinutesLabel = "هدف، دقیقه",
    targetTasksLabel = "هدف، کار",
    deadlineTitle = "مهلت",
    saveTitle = "ذخیره",
    titleError = "نام هدف را وارد کنید",
    scopeError = "دسته موردنظر را انتخاب کنید",
    targetError = "مقداری بزرگ‌تر از صفر وارد کنید",
)

private val frenchGoalStrings = englishGoalStrings.copy(
    createGoalTitle = "Nouvel objectif",
    editGoalTitle = "Modifier l'objectif",
    titleLabel = "Nom",
    titlePlaceholder = "Par exemple, Anglais",
    scopeTitle = "Éléments à comptabiliser",
    allTasksTitle = "Toutes les tâches",
    categoryScopeTitle = "Catégorie",
    subCategoryScopeTitle = "Sous-catégorie",
    chooseCategoryTitle = "Choisir une catégorie",
    chooseSubCategoryTitle = "Choisir une sous-catégorie",
    metricTitle = "Mesure",
    durationMetricTitle = "Durée",
    taskCountMetricTitle = "Nombre de tâches",
    directionTitle = "Type d’objectif",
    atLeastTitle = "Au moins",
    atMostTitle = "Au maximum",
    targetMinutesLabel = "Objectif, minutes",
    targetTasksLabel = "Objectif, tâches",
    deadlineTitle = "Date limite",
    saveTitle = "Enregistrer",
    titleError = "Saisissez un nom d’objectif",
    scopeError = "Choisissez la catégorie à comptabiliser",
    targetError = "Saisissez une valeur supérieure à zéro",
)

private val brazilianPortugueseGoalStrings = englishGoalStrings.copy(
    createGoalTitle = "Nova meta",
    editGoalTitle = "Editar meta",
    titleLabel = "Nome",
    titlePlaceholder = "Por exemplo, Inglês",
    scopeTitle = "O que contabilizar",
    allTasksTitle = "Todas as tarefas",
    categoryScopeTitle = "Categoria",
    subCategoryScopeTitle = "Subcategoria",
    chooseCategoryTitle = "Escolher categoria",
    chooseSubCategoryTitle = "Escolher subcategoria",
    metricTitle = "Métrica",
    durationMetricTitle = "Duração",
    taskCountMetricTitle = "Quantidade de tarefas",
    directionTitle = "Tipo de meta",
    atLeastTitle = "Pelo menos",
    atMostTitle = "No máximo",
    targetMinutesLabel = "Meta, minutos",
    targetTasksLabel = "Meta, tarefas",
    deadlineTitle = "Prazo",
    saveTitle = "Salvar",
    titleError = "Digite o nome da meta",
    scopeError = "Escolha a categoria a contabilizar",
    targetError = "Digite um valor maior que zero",
)

private val turkishGoalStrings = englishGoalStrings.copy(
    createGoalTitle = "Yeni hedef",
    editGoalTitle = "Hedefi düzenle",
    titleLabel = "Ad",
    titlePlaceholder = "Örneğin, İngilizce",
    scopeTitle = "Neler hesaba katılacak",
    allTasksTitle = "Tüm görevler",
    categoryScopeTitle = "Kategori",
    subCategoryScopeTitle = "Alt kategori",
    chooseCategoryTitle = "Kategori seç",
    chooseSubCategoryTitle = "Alt kategori seç",
    metricTitle = "Ölçüm",
    durationMetricTitle = "Süre",
    taskCountMetricTitle = "Görev sayısı",
    directionTitle = "Hedef türü",
    atLeastTitle = "En az",
    atMostTitle = "En fazla",
    targetMinutesLabel = "Hedef, dakika",
    targetTasksLabel = "Hedef, görev",
    deadlineTitle = "Son tarih",
    saveTitle = "Kaydet",
    titleError = "Hedef adını girin",
    scopeError = "Hesaba katılacak kategoriyi seçin",
    targetError = "Sıfırdan büyük bir değer girin",
)

private val vietnameseGoalStrings = englishGoalStrings.copy(
    createGoalTitle = "Mục tiêu mới",
    editGoalTitle = "Sửa mục tiêu",
    titleLabel = "Tên",
    titlePlaceholder = "Ví dụ: Tiếng Anh",
    scopeTitle = "Nội dung cần tính",
    allTasksTitle = "Tất cả nhiệm vụ",
    categoryScopeTitle = "Danh mục",
    subCategoryScopeTitle = "Danh mục con",
    chooseCategoryTitle = "Chọn danh mục",
    chooseSubCategoryTitle = "Chọn danh mục con",
    metricTitle = "Chỉ số",
    durationMetricTitle = "Thời lượng",
    taskCountMetricTitle = "Số nhiệm vụ",
    directionTitle = "Loại mục tiêu",
    atLeastTitle = "Ít nhất",
    atMostTitle = "Không quá",
    targetMinutesLabel = "Mục tiêu, phút",
    targetTasksLabel = "Mục tiêu, nhiệm vụ",
    deadlineTitle = "Hạn chót",
    saveTitle = "Lưu",
    titleError = "Nhập tên mục tiêu",
    scopeError = "Chọn danh mục cần tính",
    targetError = "Nhập giá trị lớn hơn không",
)

private val polishGoalStrings = englishGoalStrings.copy(
    createGoalTitle = "Nowy cel",
    editGoalTitle = "Edytuj cel",
    titleLabel = "Nazwa",
    titlePlaceholder = "Na przykład: Angielski",
    scopeTitle = "Co uwzględniać",
    allTasksTitle = "Wszystkie zadania",
    categoryScopeTitle = "Kategoria",
    subCategoryScopeTitle = "Podkategoria",
    chooseCategoryTitle = "Wybierz kategorię",
    chooseSubCategoryTitle = "Wybierz podkategorię",
    metricTitle = "Miara",
    durationMetricTitle = "Czas trwania",
    taskCountMetricTitle = "Liczba zadań",
    directionTitle = "Typ celu",
    atLeastTitle = "Co najmniej",
    atMostTitle = "Nie więcej niż",
    targetMinutesLabel = "Cel, minuty",
    targetTasksLabel = "Cel, zadania",
    deadlineTitle = "Termin",
    saveTitle = "Zapisz",
    titleError = "Wpisz nazwę celu",
    scopeError = "Wybierz kategorię do uwzględnienia",
    targetError = "Wpisz wartość większą od zera",
)

private val italianGoalStrings = englishGoalStrings.copy(
    createGoalTitle = "Nuovo obiettivo",
    editGoalTitle = "Modifica obiettivo",
    titleLabel = "Nome",
    titlePlaceholder = "Ad esempio, Inglese",
    scopeTitle = "Cosa conteggiare",
    allTasksTitle = "Tutte le attività",
    categoryScopeTitle = "Categoria",
    subCategoryScopeTitle = "Sottocategoria",
    chooseCategoryTitle = "Scegli categoria",
    chooseSubCategoryTitle = "Scegli sottocategoria",
    metricTitle = "Metrica",
    durationMetricTitle = "Durata",
    taskCountMetricTitle = "Numero di attività",
    directionTitle = "Tipo di obiettivo",
    atLeastTitle = "Almeno",
    atMostTitle = "Non più di",
    targetMinutesLabel = "Obiettivo, minuti",
    targetTasksLabel = "Obiettivo, attività",
    deadlineTitle = "Scadenza",
    saveTitle = "Salva",
    titleError = "Inserisci il nome dell’obiettivo",
    scopeError = "Scegli la categoria da conteggiare",
    targetError = "Inserisci un valore maggiore di zero",
)

private val chineseGoalStrings = englishGoalStrings.copy(
    createGoalTitle = "新目标",
    editGoalTitle = "编辑目标",
    titleLabel = "名称",
    titlePlaceholder = "例如：英语",
    scopeTitle = "统计范围",
    allTasksTitle = "所有任务",
    categoryScopeTitle = "分类",
    subCategoryScopeTitle = "子分类",
    chooseCategoryTitle = "选择分类",
    chooseSubCategoryTitle = "选择子分类",
    metricTitle = "指标",
    durationMetricTitle = "时长",
    taskCountMetricTitle = "任务数量",
    directionTitle = "目标类型",
    atLeastTitle = "至少",
    atMostTitle = "不超过",
    targetMinutesLabel = "目标，分钟",
    targetTasksLabel = "目标，任务",
    deadlineTitle = "截止日期",
    saveTitle = "保存",
    titleError = "请输入目标名称",
    scopeError = "请选择要统计的分类",
    targetError = "请输入大于零的值",
)

internal val LocalEditorGoalStrings = staticCompositionLocalOf<EditorGoalStrings> {
    error("Editor Goal Strings is not provided")
}

internal fun fetchEditorGoalStrings(language: TimePlannerLanguage) = when (language) {
    TimePlannerLanguage.EN -> englishGoalStrings
    TimePlannerLanguage.RU -> russianGoalStrings
    TimePlannerLanguage.DE -> germanGoalStrings
    TimePlannerLanguage.ES -> spanishGoalStrings
    TimePlannerLanguage.FA -> persianGoalStrings
    TimePlannerLanguage.FR -> frenchGoalStrings
    TimePlannerLanguage.PT_BR -> brazilianPortugueseGoalStrings
    TimePlannerLanguage.TR -> turkishGoalStrings
    TimePlannerLanguage.VN -> vietnameseGoalStrings
    TimePlannerLanguage.PL -> polishGoalStrings
    TimePlannerLanguage.IT -> italianGoalStrings
    TimePlannerLanguage.ZH -> chineseGoalStrings
}

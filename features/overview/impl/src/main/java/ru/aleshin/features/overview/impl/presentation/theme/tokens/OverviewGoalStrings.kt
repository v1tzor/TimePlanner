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
package ru.aleshin.features.overview.impl.presentation.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerLanguage

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal data class OverviewGoalStrings(
    val goalsTitle: String,
    val addGoalIconDesc: String,
    val historyIconDesc: String,
    val emptyGoalsTitle: String,
    val goalDetailsTitle: String,
    val goalsHistoryTitle: String,
    val emptyHistoryTitle: String,
    val editGoalIconDesc: String,
    val deleteGoalTitle: String,
    val deleteGoalConfirmation: String,
    val actualTitle: String,
    val plannedTitle: String,
    val targetTitle: String,
    val remainingTitle: String,
    val scopeUnavailableTitle: String,
    val achievedTitle: String,
    val notAchievedTitle: String,
    val exceededTitle: String,
    val expiredTitle: String,
    val inProgressTitle: String,
    val deadlineIconDesc: String,
    val emptyGoalTasksTitle: String,
    val tasksUnit: String,
    val undoTitle: String,
    val goalDeletedMessage: String,
)

private val englishGoalStrings = OverviewGoalStrings(
    goalsTitle = "Goals",
    addGoalIconDesc = "Add goal",
    historyIconDesc = "Goal history",
    emptyGoalsTitle = "No goals yet",
    goalDetailsTitle = "Goal details",
    goalsHistoryTitle = "Goal history",
    emptyHistoryTitle = "No completed goals yet",
    editGoalIconDesc = "Edit goal",
    deleteGoalTitle = "Delete goal",
    deleteGoalConfirmation = "Delete this goal? Completed history will be kept.",
    actualTitle = "Actual",
    plannedTitle = "Planned",
    targetTitle = "Target",
    remainingTitle = "Remaining",
    scopeUnavailableTitle = "Choose a new category to continue tracking",
    achievedTitle = "Achieved",
    notAchievedTitle = "Not achieved",
    exceededTitle = "Limit exceeded",
    expiredTitle = "Deadline passed",
    inProgressTitle = "In progress",
    deadlineIconDesc = "Deadline",
    emptyGoalTasksTitle = "No tasks contribute to this goal yet",
    tasksUnit = "tasks",
    undoTitle = "Undo",
    goalDeletedMessage = "Goal deleted",
)

private val russianGoalStrings = OverviewGoalStrings(
    goalsTitle = "Цели",
    addGoalIconDesc = "Добавить цель",
    historyIconDesc = "История целей",
    emptyGoalsTitle = "Целей пока нет",
    goalDetailsTitle = "Детали цели",
    goalsHistoryTitle = "История целей",
    emptyHistoryTitle = "Завершённых целей пока нет",
    editGoalIconDesc = "Редактировать цель",
    deleteGoalTitle = "Удалить цель",
    deleteGoalConfirmation = "Удалить эту цель? Завершённая история сохранится.",
    actualTitle = "Факт",
    plannedTitle = "Запланировано",
    targetTitle = "Цель",
    remainingTitle = "Осталось",
    scopeUnavailableTitle = "Выберите новую категорию, чтобы продолжить отслеживание",
    achievedTitle = "Выполнено",
    notAchievedTitle = "Не выполнено",
    exceededTitle = "Лимит превышен",
    expiredTitle = "Дедлайн истёк",
    inProgressTitle = "В процессе",
    deadlineIconDesc = "Дедлайн",
    emptyGoalTasksTitle = "Пока ни одна задача не влияет на эту цель",
    tasksUnit = "задач",
    undoTitle = "Отменить",
    goalDeletedMessage = "Цель удалена",
)

private val germanGoalStrings = englishGoalStrings.copy(
    goalsTitle = "Ziele",
    addGoalIconDesc = "Ziel hinzufügen",
    historyIconDesc = "Zielverlauf",
    emptyGoalsTitle = "Noch keine Ziele",
    goalDetailsTitle = "Zieldetails",
    goalsHistoryTitle = "Zielverlauf",
    emptyHistoryTitle = "Noch keine abgeschlossenen Zeiträume",
    editGoalIconDesc = "Ziel bearbeiten",
    deleteGoalTitle = "Ziel löschen",
    deleteGoalConfirmation = "Dieses Ziel löschen? Der abgeschlossene Verlauf bleibt erhalten.",
    actualTitle = "Tatsächlich",
    plannedTitle = "Geplant",
    targetTitle = "Ziel",
    remainingTitle = "Verbleibend",
    scopeUnavailableTitle = "Wähle eine neue Kategorie, um die Erfassung fortzusetzen",
    achievedTitle = "Erreicht",
    notAchievedTitle = "Nicht erreicht",
    exceededTitle = "Limit überschritten",
    inProgressTitle = "In Bearbeitung",
    tasksUnit = "Aufgaben",
    undoTitle = "Rückgängig",
    goalDeletedMessage = "Ziel gelöscht",
)

private val spanishGoalStrings = englishGoalStrings.copy(
    goalsTitle = "Objetivos",
    addGoalIconDesc = "Añadir objetivo",
    historyIconDesc = "Historial de objetivos",
    emptyGoalsTitle = "Aún no hay objetivos",
    goalDetailsTitle = "Detalles del objetivo",
    goalsHistoryTitle = "Historial de objetivos",
    emptyHistoryTitle = "Aún no hay periodos completados",
    editGoalIconDesc = "Editar objetivo",
    deleteGoalTitle = "Eliminar objetivo",
    deleteGoalConfirmation = "¿Eliminar este objetivo? Se conservará el historial completado.",
    actualTitle = "Real",
    plannedTitle = "Planificado",
    targetTitle = "Objetivo",
    remainingTitle = "Restante",
    scopeUnavailableTitle = "Elige una categoría nueva para continuar el seguimiento",
    achievedTitle = "Conseguido",
    notAchievedTitle = "No conseguido",
    exceededTitle = "Límite superado",
    inProgressTitle = "En progreso",
    tasksUnit = "tareas",
    undoTitle = "Deshacer",
    goalDeletedMessage = "Objetivo eliminado",
)

private val persianGoalStrings = englishGoalStrings.copy(
    goalsTitle = "هدف‌ها",
    addGoalIconDesc = "افزودن هدف",
    historyIconDesc = "تاریخچه هدف‌ها",
    emptyGoalsTitle = "هنوز هدفی وجود ندارد",
    goalDetailsTitle = "جزئیات هدف",
    goalsHistoryTitle = "تاریخچه هدف‌ها",
    emptyHistoryTitle = "هنوز دوره تکمیل‌شده‌ای وجود ندارد",
    editGoalIconDesc = "ویرایش هدف",
    deleteGoalTitle = "حذف هدف",
    deleteGoalConfirmation = "این هدف حذف شود؟ تاریخچه تکمیل‌شده حفظ می‌شود.",
    actualTitle = "واقعی",
    plannedTitle = "برنامه‌ریزی‌شده",
    targetTitle = "هدف",
    remainingTitle = "باقی‌مانده",
    scopeUnavailableTitle = "برای ادامه پیگیری، دسته جدیدی انتخاب کنید",
    achievedTitle = "انجام شد",
    notAchievedTitle = "انجام نشد",
    exceededTitle = "از حد مجاز بیشتر",
    inProgressTitle = "در حال انجام",
    tasksUnit = "کار",
    undoTitle = "واگرد",
    goalDeletedMessage = "هدف حذف شد",
)

private val frenchGoalStrings = englishGoalStrings.copy(
    goalsTitle = "Objectifs",
    addGoalIconDesc = "Ajouter un objectif",
    historyIconDesc = "Historique des objectifs",
    emptyGoalsTitle = "Aucun objectif pour le moment",
    goalDetailsTitle = "Détails de l’objectif",
    goalsHistoryTitle = "Historique des objectifs",
    emptyHistoryTitle = "Aucune période terminée",
    editGoalIconDesc = "Modifier l’objectif",
    deleteGoalTitle = "Supprimer l’objectif",
    deleteGoalConfirmation = "Supprimer cet objectif ? L’historique terminé sera conservé.",
    actualTitle = "Réel",
    plannedTitle = "Planifié",
    targetTitle = "Objectif",
    remainingTitle = "Restant",
    scopeUnavailableTitle = "Choisissez une nouvelle catégorie pour poursuivre le suivi",
    achievedTitle = "Atteint",
    notAchievedTitle = "Non atteint",
    exceededTitle = "Limite dépassée",
    inProgressTitle = "En cours",
    tasksUnit = "tâches",
    undoTitle = "Annuler",
    goalDeletedMessage = "Objectif supprimé",
)

private val brazilianPortugueseGoalStrings = englishGoalStrings.copy(
    goalsTitle = "Metas",
    addGoalIconDesc = "Adicionar meta",
    historyIconDesc = "Histórico de metas",
    emptyGoalsTitle = "Ainda não há metas",
    goalDetailsTitle = "Detalhes da meta",
    goalsHistoryTitle = "Histórico de metas",
    emptyHistoryTitle = "Ainda não há períodos concluídos",
    editGoalIconDesc = "Editar meta",
    deleteGoalTitle = "Excluir meta",
    deleteGoalConfirmation = "Excluir esta meta? O histórico concluído será mantido.",
    actualTitle = "Real",
    plannedTitle = "Planejado",
    targetTitle = "Meta",
    remainingTitle = "Restante",
    scopeUnavailableTitle = "Escolha uma nova categoria para continuar acompanhando",
    achievedTitle = "Alcançada",
    notAchievedTitle = "Não alcançada",
    exceededTitle = "Limite excedido",
    inProgressTitle = "Em andamento",
    tasksUnit = "tarefas",
    undoTitle = "Desfazer",
    goalDeletedMessage = "Meta excluída",
)

private val turkishGoalStrings = englishGoalStrings.copy(
    goalsTitle = "Hedefler",
    addGoalIconDesc = "Hedef ekle",
    historyIconDesc = "Hedef geçmişi",
    emptyGoalsTitle = "Henüz hedef yok",
    goalDetailsTitle = "Hedef ayrıntıları",
    goalsHistoryTitle = "Hedef geçmişi",
    emptyHistoryTitle = "Henüz tamamlanan dönem yok",
    editGoalIconDesc = "Hedefi düzenle",
    deleteGoalTitle = "Hedefi sil",
    deleteGoalConfirmation = "Bu hedef silinsin mi? Tamamlanan geçmiş korunacak.",
    actualTitle = "Gerçekleşen",
    plannedTitle = "Planlanan",
    targetTitle = "Hedef",
    remainingTitle = "Kalan",
    scopeUnavailableTitle = "Takibe devam etmek için yeni bir kategori seçin",
    achievedTitle = "Tamamlandı",
    notAchievedTitle = "Tamamlanmadı",
    exceededTitle = "Sınır aşıldı",
    inProgressTitle = "Devam ediyor",
    tasksUnit = "görev",
    undoTitle = "Geri al",
    goalDeletedMessage = "Hedef silindi",
)

private val vietnameseGoalStrings = englishGoalStrings.copy(
    goalsTitle = "Mục tiêu",
    addGoalIconDesc = "Thêm mục tiêu",
    historyIconDesc = "Lịch sử mục tiêu",
    emptyGoalsTitle = "Chưa có mục tiêu",
    goalDetailsTitle = "Chi tiết mục tiêu",
    goalsHistoryTitle = "Lịch sử mục tiêu",
    emptyHistoryTitle = "Chưa có giai đoạn hoàn tất",
    editGoalIconDesc = "Chỉnh sửa mục tiêu",
    deleteGoalTitle = "Xóa mục tiêu",
    deleteGoalConfirmation = "Xóa mục tiêu này? Lịch sử đã hoàn tất sẽ được giữ lại.",
    actualTitle = "Thực tế",
    plannedTitle = "Đã lên kế hoạch",
    targetTitle = "Mục tiêu",
    remainingTitle = "Còn lại",
    scopeUnavailableTitle = "Chọn danh mục mới để tiếp tục theo dõi",
    achievedTitle = "Đã đạt",
    notAchievedTitle = "Chưa đạt",
    exceededTitle = "Vượt giới hạn",
    inProgressTitle = "Đang thực hiện",
    tasksUnit = "nhiệm vụ",
    undoTitle = "Hoàn tác",
    goalDeletedMessage = "Đã xóa mục tiêu",
)

private val polishGoalStrings = englishGoalStrings.copy(
    goalsTitle = "Cele",
    addGoalIconDesc = "Dodaj cel",
    historyIconDesc = "Historia celów",
    emptyGoalsTitle = "Brak celów",
    goalDetailsTitle = "Szczegóły celu",
    goalsHistoryTitle = "Historia celów",
    emptyHistoryTitle = "Brak zakończonych okresów",
    editGoalIconDesc = "Edytuj cel",
    deleteGoalTitle = "Usuń cel",
    deleteGoalConfirmation = "Usunąć ten cel? Historia zakończonych okresów zostanie zachowana.",
    actualTitle = "Wykonano",
    plannedTitle = "Zaplanowano",
    targetTitle = "Cel",
    remainingTitle = "Pozostało",
    scopeUnavailableTitle = "Wybierz nową kategorię, aby kontynuować śledzenie",
    achievedTitle = "Osiągnięto",
    notAchievedTitle = "Nie osiągnięto",
    exceededTitle = "Limit przekroczony",
    inProgressTitle = "W trakcie",
    tasksUnit = "zadania",
    undoTitle = "Cofnij",
    goalDeletedMessage = "Cel usunięty",
)

private val italianGoalStrings = englishGoalStrings.copy(
    goalsTitle = "Obiettivi",
    addGoalIconDesc = "Aggiungi obiettivo",
    historyIconDesc = "Cronologia obiettivi",
    emptyGoalsTitle = "Nessun obiettivo",
    goalDetailsTitle = "Dettagli obiettivo",
    goalsHistoryTitle = "Cronologia obiettivi",
    emptyHistoryTitle = "Nessun periodo completato",
    editGoalIconDesc = "Modifica obiettivo",
    deleteGoalTitle = "Elimina obiettivo",
    deleteGoalConfirmation = "Eliminare questo obiettivo? La cronologia completata sarà conservata.",
    actualTitle = "Effettivo",
    plannedTitle = "Pianificato",
    targetTitle = "Obiettivo",
    remainingTitle = "Rimanente",
    scopeUnavailableTitle = "Scegli una nuova categoria per continuare il monitoraggio",
    achievedTitle = "Raggiunto",
    notAchievedTitle = "Non raggiunto",
    exceededTitle = "Limite superato",
    inProgressTitle = "In corso",
    tasksUnit = "attività",
    undoTitle = "Annulla",
    goalDeletedMessage = "Obiettivo eliminato",
)

private val chineseGoalStrings = englishGoalStrings.copy(
    goalsTitle = "目标",
    addGoalIconDesc = "添加目标",
    historyIconDesc = "目标历史",
    emptyGoalsTitle = "暂无目标",
    goalDetailsTitle = "目标详情",
    goalsHistoryTitle = "目标历史",
    emptyHistoryTitle = "暂无已完成周期",
    editGoalIconDesc = "编辑目标",
    deleteGoalTitle = "删除目标",
    deleteGoalConfirmation = "删除此目标？已完成的历史记录将会保留。",
    actualTitle = "实际",
    plannedTitle = "计划",
    targetTitle = "目标",
    remainingTitle = "剩余",
    scopeUnavailableTitle = "请选择新分类以继续跟踪",
    achievedTitle = "已达成",
    notAchievedTitle = "未达成",
    exceededTitle = "超出限制",
    inProgressTitle = "进行中",
    tasksUnit = "个任务",
    undoTitle = "撤销",
    goalDeletedMessage = "目标已删除",
)

internal val LocalOverviewGoalStrings = staticCompositionLocalOf<OverviewGoalStrings> {
    error("Overview goal strings are not provided")
}

internal fun fetchOverviewGoalStrings(language: TimePlannerLanguage) = when (language) {
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

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
package ru.aleshin.features.templates.impl.presentation.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerLanguage

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
internal data class TemplatesStrings(
    val topAppBarMenuIconDesc: String,
    val navToBackTitle: String,
    val topAppBarTemplatesTitle: String,
    val dialogCreateTitle: String,
    val emptyListTitle: String,
    val otherError: String,
    val warningDeleteTemplateText: String,
    val warningDeleteRepeatTemplateText: String,
    val sortedTypeTitle: String,
    val sortedTypeDate: String,
    val sortedTypeCategories: String,
    val sortedTypeDuration: String,
    val notificationEnabledTitle: String,
    val notificationDisabledTitle: String,
    val statisticsActiveTitle: String,
    val statisticsDisabledTitle: String,
    val addTemplatesFabTitle: String,
    val templateEditorHeader: String,
    val mainCategoryLabel: String,
    val subCategoryLabel: String,
    val startTimeLabel: String,
    val endTimeLabel: String,
    val notificationLabel: String,
    val statisticsLabel: String,
    val priorityLabel: String,
    val subCategoryEmptyTitle: String,
)

internal val russianTemplatesString = TemplatesStrings(
    topAppBarMenuIconDesc = "Открыть меню",
    navToBackTitle = "Назад",
    topAppBarTemplatesTitle = "Шаблоны",
    dialogCreateTitle = "Создать",
    emptyListTitle = "Список пуст",
    otherError = "Ошибка! Обратитесь к разработчику.",
    warningDeleteTemplateText = "Вы уверены, что хотите удалить этот шаблон?",
    warningDeleteRepeatTemplateText = "Вы уверены, что хотите удалить этот шаблон? " +
        "Будущие задачи, созданные его повторениями, также будут удалены.",
    sortedTypeTitle = "Сортировать",
    sortedTypeDate = "По дате",
    sortedTypeCategories = "По категориям",
    sortedTypeDuration = "По длительности",
    notificationEnabledTitle = "Включено",
    notificationDisabledTitle = "Выключено",
    statisticsActiveTitle = "Активна",
    statisticsDisabledTitle = "Выключена",
    addTemplatesFabTitle = "+",
    templateEditorHeader = "Шаблон",
    mainCategoryLabel = "Категория",
    subCategoryLabel = "Подкатегория",
    startTimeLabel = "Начало",
    endTimeLabel = "Конец",
    notificationLabel = "Отправка уведомлений",
    statisticsLabel = "Учёт статистики",
    priorityLabel = "Приоритет",
    subCategoryEmptyTitle = "Отсутствует",
)

internal val englishTemplatesString = TemplatesStrings(
    topAppBarMenuIconDesc = "Open menu",
    navToBackTitle = "Back",
    topAppBarTemplatesTitle = "Templates",
    dialogCreateTitle = "Create",
    emptyListTitle = "List is empty",
    otherError = "Something went wrong. Contact the developer for help.",
    warningDeleteTemplateText = "Are you sure you want to delete this template?",
    warningDeleteRepeatTemplateText = "Are you sure you want to delete this template? " +
        "Future tasks created by its repeats will also be deleted.",
    sortedTypeTitle = "Sort",
    sortedTypeDate = "By date",
    sortedTypeCategories = "By category",
    sortedTypeDuration = "By duration",
    notificationEnabledTitle = "Enabled",
    notificationDisabledTitle = "Disabled",
    statisticsActiveTitle = "Active",
    statisticsDisabledTitle = "Disabled",
    addTemplatesFabTitle = "+",
    templateEditorHeader = "Template",
    mainCategoryLabel = "Category",
    subCategoryLabel = "Subcategory",
    startTimeLabel = "Start",
    endTimeLabel = "End",
    notificationLabel = "Sending notification",
    statisticsLabel = "Include task in statistics",
    priorityLabel = "Priority",
    subCategoryEmptyTitle = "Absent",
)

internal val persianTemplatesString = TemplatesStrings(
    topAppBarMenuIconDesc = "بازکردن منو",
    navToBackTitle = "بازگشت",
    topAppBarTemplatesTitle = "قالب ها",
    dialogCreateTitle = "ایجاد",
    emptyListTitle = "لیست خالی است",
    otherError = "خطا! با توسعه دهنده تماس بگیرید.",
    warningDeleteTemplateText = "آیا مطمئن هستید که می‌خواهید این قالب را حذف کنید؟",
    warningDeleteRepeatTemplateText = "آیا مطمئن هستید که می‌خواهید این قالب را حذف کنید؟ " +
        "کارهای آینده‌ای که از تکرارهای آن ساخته شده‌اند نیز حذف می‌شوند.",
    sortedTypeTitle = "مرتب شده",
    sortedTypeDate = "بر اساس تاریخ",
    sortedTypeCategories = "بر اساس طبقه بندی",
    sortedTypeDuration = "بر اساس مدت زمان",
    notificationEnabledTitle = "فعال",
    notificationDisabledTitle = "غیرفعال",
    statisticsActiveTitle = "فعال",
    statisticsDisabledTitle = "غیرفعال",
    addTemplatesFabTitle = "+",
    templateEditorHeader = "قالب",
    mainCategoryLabel = "دسته بندی",
    subCategoryLabel = "زیرمجموعه",
    startTimeLabel = "شروع",
    endTimeLabel = "پایان",
    notificationLabel = "ارسال اعلان",
    statisticsLabel = "وظیفه را در آمار وارد کنید",
    priorityLabel = "اولویت ویژه",
    subCategoryEmptyTitle = "خالی",
)

internal val germanTemplatesString = TemplatesStrings(
    topAppBarMenuIconDesc = "Menü öffnen",
    navToBackTitle = "Zurück",
    topAppBarTemplatesTitle = "Muster",
    dialogCreateTitle = "Erstellen",
    emptyListTitle = "Die Liste ist leer",
    otherError = "Fehler! Wenden Sie sich an den Entwickler.",
    warningDeleteTemplateText = "Möchten Sie diese Vorlage wirklich löschen?",
    warningDeleteRepeatTemplateText = "Möchten Sie diese Vorlage wirklich löschen? " +
        "Zukünftige Aufgaben aus ihren Wiederholungen werden ebenfalls gelöscht.",
    sortedTypeTitle = "Sortieren",
    sortedTypeDate = "Nach Datum",
    sortedTypeCategories = "Nach Kategorie",
    sortedTypeDuration = "Nach Dauer",
    notificationEnabledTitle = "Eingeschaltet",
    notificationDisabledTitle = "Abgeschaltet",
    statisticsActiveTitle = "Aktiv",
    statisticsDisabledTitle = "Abgeschaltet",
    addTemplatesFabTitle = "+",
    templateEditorHeader = "Muster",
    mainCategoryLabel = "Kategorie",
    subCategoryLabel = "Unterkategorie",
    startTimeLabel = "Anfang",
    endTimeLabel = "Ende",
    notificationLabel = "Benachrichtigungen",
    statisticsLabel = "Statistik",
    priorityLabel = "Priorität",
    subCategoryEmptyTitle = "Leer",
)

internal val spanishTemplatesString = TemplatesStrings(
    topAppBarMenuIconDesc = "Abrir menú",
    navToBackTitle = "Volver",
    topAppBarTemplatesTitle = "Plantillas",
    dialogCreateTitle = "Crear",
    emptyListTitle = "Lista vacía",
    otherError = "¡Error! Contacta al desarrollador.",
    warningDeleteTemplateText = "¿Seguro que quieres eliminar esta plantilla?",
    warningDeleteRepeatTemplateText = "¿Seguro que quieres eliminar esta plantilla? " +
        "También se eliminarán las tareas futuras creadas por sus repeticiones.",
    sortedTypeTitle = "Ordenado",
    sortedTypeDate = "Por fecha",
    sortedTypeCategories = "Por categoría",
    sortedTypeDuration = "Por duración",
    notificationEnabledTitle = "Activada",
    notificationDisabledTitle = "Desactivada",
    statisticsActiveTitle = "Activada",
    statisticsDisabledTitle = "Desactivada",
    addTemplatesFabTitle = "+",
    templateEditorHeader = "Plantilla",
    mainCategoryLabel = "Categoría",
    subCategoryLabel = "Subcategoría",
    startTimeLabel = "Empieza",
    endTimeLabel = "Termina",
    notificationLabel = "Enviar notificación",
    statisticsLabel = "Incluir en estadísticas",
    priorityLabel = "Prioridad",
    subCategoryEmptyTitle = "Ninguna",
)

internal val frenchTemplatesString = TemplatesStrings(
    topAppBarMenuIconDesc = "Ouvrir le menu",
    navToBackTitle = "Retour",
    topAppBarTemplatesTitle = "Modèles",
    dialogCreateTitle = "Créer",
    emptyListTitle = "La liste est vide",
    otherError = "Erreur! Veuiller contacter le developpeur.",
    warningDeleteTemplateText = "Voulez-vous vraiment supprimer ce modèle ?",
    warningDeleteRepeatTemplateText = "Voulez-vous vraiment supprimer ce modèle ? " +
        "Les futures tâches créées par ses répétitions seront aussi supprimées.",
    sortedTypeTitle = "Trié",
    sortedTypeDate = "Par date",
    sortedTypeCategories = "Par catégorie",
    sortedTypeDuration = "Par durée",
    notificationEnabledTitle = "Activé",
    notificationDisabledTitle = "Désactivé",
    statisticsActiveTitle = "Activé",
    statisticsDisabledTitle = "Désactivé",
    addTemplatesFabTitle = "+",
    templateEditorHeader = "Modèle",
    mainCategoryLabel = "Catégorie",
    subCategoryLabel = "Sous-catégorie",
    startTimeLabel = "Début",
    endTimeLabel = "Fin",
    notificationLabel = "Envoi d'une notification",
    statisticsLabel = "Inclure dans les statistiques",
    priorityLabel = "Priorité",
    subCategoryEmptyTitle = "Absent",
)
internal val brazilianPortugueseTemplatesString = TemplatesStrings(
    topAppBarMenuIconDesc = "Abrir menu",
    navToBackTitle = "Voltar",
    topAppBarTemplatesTitle = "Templates",
    dialogCreateTitle = "Criar",
    emptyListTitle = "Lista está vazia",
    otherError = "Erro! Contate o desenvolvedor.",
    warningDeleteTemplateText = "Tem certeza que deseja excluir este template?",
    warningDeleteRepeatTemplateText = "Tem certeza que deseja excluir este template? " +
        "As tarefas futuras criadas pelas repetições dele também serão excluídas.",
    sortedTypeTitle = "Classificado",
    sortedTypeDate = "Por dia",
    sortedTypeCategories = "Por categoria",
    sortedTypeDuration = "Por duração",
    notificationEnabledTitle = "Ativado",
    notificationDisabledTitle = "Desativado",
    statisticsActiveTitle = "Ativado",
    statisticsDisabledTitle = "Desativado",
    addTemplatesFabTitle = "+",
    templateEditorHeader = "Template",
    mainCategoryLabel = "Categoria",
    subCategoryLabel = "Subcategoria",
    startTimeLabel = "Iniciar",
    endTimeLabel = "Terminar",
    notificationLabel = "Enviar notificação",
    statisticsLabel = "Incluir tarefa nas estatísticas",
    priorityLabel = "Prioridade",
    subCategoryEmptyTitle = "Faltou",
)

internal val turkishTemplatesString = TemplatesStrings(
    topAppBarMenuIconDesc = "Menüyü aç",
    navToBackTitle = "Geri",
    topAppBarTemplatesTitle = "Şablonlar",
    dialogCreateTitle = "Oluştur",
    emptyListTitle = "Liste boş",
    otherError = "Hata! Geliştirici ile iletişime geçin.",
    warningDeleteTemplateText = "Bu şablonu silmek istediğinizden emin misiniz?",
    warningDeleteRepeatTemplateText = "Bu şablonu silmek istediğinizden emin misiniz? " +
        "Tekrarlarından oluşturulan gelecekteki görevler de silinir.",
    sortedTypeTitle = "Sıralı",
    sortedTypeDate = "Tarihe göre",
    sortedTypeCategories = "Kategoriye göre",
    sortedTypeDuration = "Süreye göre",
    notificationEnabledTitle = "Etkin",
    notificationDisabledTitle = "Devre dışı",
    statisticsActiveTitle = "Aktif",
    statisticsDisabledTitle = "Devre dışı",
    addTemplatesFabTitle = "+",
    templateEditorHeader = "Şablon",
    mainCategoryLabel = "Kategori",
    subCategoryLabel = "Alt kategori",
    startTimeLabel = "Başlangıç",
    endTimeLabel = "Bitiş",
    notificationLabel = "Bildirim gönderme",
    statisticsLabel = "Görevi istatistiklere dahil et",
    priorityLabel = "Öncelik",
    subCategoryEmptyTitle = "Yok",
)

internal val vietnameseTemplatesString = TemplatesStrings(
    topAppBarMenuIconDesc = "Mở thực đơn",
    navToBackTitle = "Quay lại",
    topAppBarTemplatesTitle = "Mẫu",
    dialogCreateTitle = "Tạo",
    emptyListTitle = "Danh sách trống",
    otherError = "Lỗi! Hãy liên hệ với nhà phát triển.",
    warningDeleteTemplateText = "Bạn có chắc chắn muốn xóa mẫu này không?",
    warningDeleteRepeatTemplateText = "Bạn có chắc chắn muốn xóa mẫu này không? " +
        "Các nhiệm vụ tương lai được tạo từ lặp lại của mẫu cũng sẽ bị xóa.",
    sortedTypeTitle = "Đã sắp xếp",
    sortedTypeDate = "Theo ngày",
    sortedTypeCategories = "Theo danh mục",
    sortedTypeDuration = "Theo thời lượng",
    notificationEnabledTitle = "Đã bật",
    notificationDisabledTitle = "Đã tắt",
    statisticsActiveTitle = "Kích hoạt",
    statisticsDisabledTitle = "Đã tắt",
    addTemplatesFabTitle = "+",
    templateEditorHeader = "Mẫu",
    mainCategoryLabel = "Danh mục",
    subCategoryLabel = "Danh mục con",
    startTimeLabel = "Bắt đầu",
    endTimeLabel = "Kết thúc",
    notificationLabel = "Gửi thông báo",
    statisticsLabel = "Đưa nhiệm vụ vào thống kê",
    priorityLabel = "Ưu tiên",
    subCategoryEmptyTitle = "Vắng mặt",
)

internal val polishTemplatesString = TemplatesStrings(
    topAppBarMenuIconDesc = "Otwórz menu",
    navToBackTitle = "Wróć",
    topAppBarTemplatesTitle = "Szablony",
    dialogCreateTitle = "Utwórz",
    emptyListTitle = "Lista jest pusta",
    otherError = "Coś poszło nie tak. Skontaktuj się z deweloperem by otrzymać pomoc.",
    warningDeleteTemplateText = "Czy na pewno chcesz usunąć ten szablon?",
    warningDeleteRepeatTemplateText = "Czy na pewno chcesz usunąć ten szablon? " +
        "Przyszłe zadania utworzone przez jego powtórzenia też zostaną usunięte.",
    sortedTypeTitle = "Posortowane",
    sortedTypeDate = "Według daty",
    sortedTypeCategories = "Według kategorii",
    sortedTypeDuration = "Według czasu trwania",
    notificationEnabledTitle = "Włączone",
    notificationDisabledTitle = "Wyłączone",
    statisticsActiveTitle = "Włączone",
    statisticsDisabledTitle = "Wyłączone",
    addTemplatesFabTitle = "+",
    templateEditorHeader = "Szablon",
    mainCategoryLabel = "Kategoria",
    subCategoryLabel = "Podkategoria",
    startTimeLabel = "Rozpoczęcie",
    endTimeLabel = "Zakończenie",
    notificationLabel = "Wysyłanie powiadomienia",
    statisticsLabel = "Dołącz zadanie do statystyk",
    priorityLabel = "Priorytet",
    subCategoryEmptyTitle = "Brak",
)

internal val italianTemplatesString = TemplatesStrings(
    topAppBarMenuIconDesc = "Apri menu",
    navToBackTitle = "Indietro",
    topAppBarTemplatesTitle = "Template",
    dialogCreateTitle = "Crea",
    emptyListTitle = "La lista è vuota",
    otherError = "Qualcosa è andato storto. Contatta lo sviluppatore per ottenere assistenza.",
    warningDeleteTemplateText = "Sei sicuro di voler eliminare questo template?",
    warningDeleteRepeatTemplateText = "Sei sicuro di voler eliminare questo template? " +
        "Saranno eliminate anche le attività future create dalle sue ripetizioni.",
    sortedTypeTitle = "Ordina",
    sortedTypeDate = "Per data",
    sortedTypeCategories = "Per categoria",
    sortedTypeDuration = "Per durata",
    notificationEnabledTitle = "Abilitato",
    notificationDisabledTitle = "Disabilitato",
    statisticsActiveTitle = "Attivo",
    statisticsDisabledTitle = "Disabilitato",
    addTemplatesFabTitle = "+",
    templateEditorHeader = "Template",
    mainCategoryLabel = "Categoria",
    subCategoryLabel = "Sottocategoria",
    startTimeLabel = "Inizio",
    endTimeLabel = "Fine",
    notificationLabel = "Invia una notifica",
    statisticsLabel = "Includi il task nelle statistiche",
    priorityLabel = "Priorità",
    subCategoryEmptyTitle = "Assente",
)


internal val chineseTemplatesString = TemplatesStrings(
    topAppBarMenuIconDesc = "打开菜单",
    navToBackTitle = "返回",
    topAppBarTemplatesTitle = "模板",
    dialogCreateTitle = "创建",
    emptyListTitle = "列表为空",
    otherError = "出现问题。请联系开发者获取帮助。",
    warningDeleteTemplateText = "确定要删除此模板吗？",
    warningDeleteRepeatTemplateText = "确定要删除此模板吗？由其重复规则创建的未来任务也会被删除。",
    sortedTypeTitle = "排序",
    sortedTypeDate = "按日期",
    sortedTypeCategories = "按分类",
    sortedTypeDuration = "按时长",
    notificationEnabledTitle = "已开启",
    notificationDisabledTitle = "已关闭",
    statisticsActiveTitle = "已启用",
    statisticsDisabledTitle = "已关闭",
    addTemplatesFabTitle = "+",
    templateEditorHeader = "模板",
    mainCategoryLabel = "分类",
    subCategoryLabel = "子分类",
    startTimeLabel = "开始",
    endTimeLabel = "结束",
    notificationLabel = "发送通知",
    statisticsLabel = "计入统计",
    priorityLabel = "优先级",
    subCategoryEmptyTitle = "无",
)

internal val LocalTemplatesStrings = staticCompositionLocalOf<TemplatesStrings> {
    error("Templates Strings is not provided")
}

internal fun fetchTemplatesStrings(language: TimePlannerLanguage) = when (language) {
    TimePlannerLanguage.EN -> englishTemplatesString
    TimePlannerLanguage.RU -> russianTemplatesString
    TimePlannerLanguage.DE -> germanTemplatesString
    TimePlannerLanguage.ES -> spanishTemplatesString
    TimePlannerLanguage.FA -> persianTemplatesString
    TimePlannerLanguage.FR -> frenchTemplatesString
    TimePlannerLanguage.PT_BR -> brazilianPortugueseTemplatesString
    TimePlannerLanguage.TR -> turkishTemplatesString
    TimePlannerLanguage.VN -> vietnameseTemplatesString
    TimePlannerLanguage.PL -> polishTemplatesString
    TimePlannerLanguage.IT -> italianTemplatesString
    TimePlannerLanguage.ZH -> chineseTemplatesString
}


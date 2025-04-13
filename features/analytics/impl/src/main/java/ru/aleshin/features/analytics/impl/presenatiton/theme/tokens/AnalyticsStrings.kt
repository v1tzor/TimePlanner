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
package ru.aleshin.features.analytics.impl.presenatiton.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.core.ui.theme.tokens.TimePlannerLanguage

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
internal data class AnalyticsStrings(
    val topAppBarTitle: String,
    val menuIconDesc: String,
    val timeTabTitle: String,
    val workLoadTabTitle: String,
    val intelligenceTabTitle: String,
    val weekTimePeriod: String,
    val monthTimePeriod: String,
    val yearTimePeriod: String,
    val halfYearTimePeriod: String,
    val timeSelectorTitle: String,
    val refreshAnalyticIconDesc: String,
    val otherAnalyticsName: String,
    val allTimeTitle: String,
    val totalCountTaskTitle: String,
    val totalTimeTaskTitle: String,
    val averageCountTaskTitle: String,
    val averageTimeTaskTitle: String,
    val planningAnalyticsTitle: String,
    val workLoadAnalyticsTitle: String,
    val categoryStatisticsTitle: String,
    val executedStatisticsTitle: String,
    val otherError: String,
)

internal val russianAnalyticsString = AnalyticsStrings(
    topAppBarTitle = "Аналитика",
    menuIconDesc = "Меню",
    timeTabTitle = "Время",
    workLoadTabTitle = "Загруженность",
    intelligenceTabTitle = "Сведения",
    weekTimePeriod = "Неделя",
    monthTimePeriod = "Месяц",
    yearTimePeriod = "Год",
    timeSelectorTitle = "Временной промежуток:",
    refreshAnalyticIconDesc = "Обновить аналитику",
    otherAnalyticsName = "Прочее",
    allTimeTitle = "Всего:",
    totalCountTaskTitle = "Количество\nзадач",
    averageCountTaskTitle = "Число задач\nв день",
    totalTimeTaskTitle = "Общее время\nзадач",
    averageTimeTaskTitle = "Среднее время\nзадачи",
    workLoadAnalyticsTitle = "Статистика загруженности",
    planningAnalyticsTitle = "Статистика планирования",
    otherError = "Ошибка! Обратитесь к разработчику.",
    halfYearTimePeriod = "Полгода",
    categoryStatisticsTitle = "Статистика категорий",
    executedStatisticsTitle = "Статистика выполнения",
)

internal val englishAnalyticsString = AnalyticsStrings(
    topAppBarTitle = "Analytics",
    menuIconDesc = "Menu",
    timeTabTitle = "Time",
    workLoadTabTitle = "Workload",
    intelligenceTabTitle = "Information",
    weekTimePeriod = "Week",
    monthTimePeriod = "Month",
    yearTimePeriod = "Year",
    timeSelectorTitle = "Time period:",
    refreshAnalyticIconDesc = "Refresh analytics",
    otherAnalyticsName = "Else",
    allTimeTitle = "Total:",
    totalCountTaskTitle = "Total tasks",
    averageCountTaskTitle = "Tasks per day",
    totalTimeTaskTitle = "Time spent\non tasks",
    averageTimeTaskTitle = "Average time\nper task",
    workLoadAnalyticsTitle = "Workload stats",
    planningAnalyticsTitle = "Planning stats",
    otherError = "Something went wrong. Contact the developer for help.",
    halfYearTimePeriod = "Half a year",
    categoryStatisticsTitle = "Category stats",
    executedStatisticsTitle = "Execution stats",
)

internal val persianAnalyticsString = AnalyticsStrings(
    topAppBarTitle = "تحلیل ها",
    menuIconDesc = "منو",
    timeTabTitle = "زمان",
    workLoadTabTitle = "حجم کار",
    intelligenceTabTitle = "اطلاعات",
    weekTimePeriod = "هفته",
    monthTimePeriod = "ماه",
    yearTimePeriod = "سال",
    timeSelectorTitle = "بازه زمانی:",
    refreshAnalyticIconDesc = "بروزرسانی تحلیل ها",
    otherAnalyticsName = "دیگر",
    allTimeTitle = "کل:",
    totalCountTaskTitle = "تعداد\n وظایف",
    averageCountTaskTitle = "تعداد\n وظایف در روز",
    totalTimeTaskTitle = "زمان کل\n ظایف",
    averageTimeTaskTitle = "میانگین کل\n وظایف",
    workLoadAnalyticsTitle = "آمار حجم کار",
    planningAnalyticsTitle = "آمار برنامه ریزی",
    otherError = "خطا! با توسعه دهنده تماس بگیرید.",
    halfYearTimePeriod = "نصف سال",
    categoryStatisticsTitle = "آمار دسته بندی",
    executedStatisticsTitle = "آمار اجرایی",
)

internal val germanAnalyticsString = AnalyticsStrings(
    topAppBarTitle = "Analyse",
    menuIconDesc = "Menü",
    timeTabTitle = "Zeit",
    workLoadTabTitle = "Auslastung",
    intelligenceTabTitle = "Daten",
    weekTimePeriod = "Woche",
    monthTimePeriod = "Monat",
    yearTimePeriod = "Jahr",
    timeSelectorTitle = "Zeitspanne:",
    refreshAnalyticIconDesc = "Analyse aktualisieren",
    otherAnalyticsName = "Ander",
    allTimeTitle = "Insgesamt:",
    totalCountTaskTitle = "Anzahl\nder Aufgaben",
    averageCountTaskTitle = "Anzahl\nder Aufgaben am Tag",
    totalTimeTaskTitle = "Gesamtzeit\nAufgaben",
    averageTimeTaskTitle = "Durchschnittliche Zeit\nAufgaben",
    workLoadAnalyticsTitle = "Workload Statistiken",
    planningAnalyticsTitle = "Planungsstatistik",
    otherError = "Fehler! Wenden Sie sich an den Entwickler.",
    halfYearTimePeriod = "Halbjahr",
    categoryStatisticsTitle = "Kategorienstatistiken",
    executedStatisticsTitle = "Ausführungsstatistiken",
)

internal val spanishAnalyticsString = AnalyticsStrings(
    topAppBarTitle = "Estadísticas",
    menuIconDesc = "Menú",
    timeTabTitle = "Tiempo",
    workLoadTabTitle = "Tareas",
    intelligenceTabTitle = "Información",
    weekTimePeriod = "Semana",
    monthTimePeriod = "Mes",
    yearTimePeriod = "Año",
    timeSelectorTitle = "Periodo de tiempo:",
    refreshAnalyticIconDesc = "Actualizar datos",
    otherAnalyticsName = "Otras",
    allTimeTitle = "Total:",
    totalCountTaskTitle = "Número de\ntareas",
    averageCountTaskTitle = "Número de\ntareas por día",
    totalTimeTaskTitle = "Duración total\nde las tareas",
    averageTimeTaskTitle = "Duración media\nde las tareas",
    workLoadAnalyticsTitle = "Estadísticas de carga de trabajo",
    planningAnalyticsTitle = "Estadísticas de planificación",
    otherError = "¡Error! Contacta al desarrollador.",
    halfYearTimePeriod = "Medio año",
    categoryStatisticsTitle = "Estadísticas por categoría",
    executedStatisticsTitle = "Estadísticas realizadas",
)

internal val frenchAnalyticsString = AnalyticsStrings(
    topAppBarTitle = "Statistiques",
    menuIconDesc = "Menu",
    timeTabTitle = "Temps",
    workLoadTabTitle = "Charge de travail",
    intelligenceTabTitle = "Information",
    weekTimePeriod = "Semaine",
    monthTimePeriod = "Mois",
    yearTimePeriod = "Année",
    timeSelectorTitle = "Période:",
    refreshAnalyticIconDesc = "Recharger les statistiques",
    otherAnalyticsName = "Autres",
    allTimeTitle = "Total:",
    totalCountTaskTitle = "Nombre\nde tâches",
    averageCountTaskTitle = "Nombre\nde tâches par jour",
    totalTimeTaskTitle = "Temps total\ndes tâches",
    averageTimeTaskTitle = "Temps moyen\npar tâches",
    workLoadAnalyticsTitle = "Statistiques de la charge de travail",
    planningAnalyticsTitle = "Statistiques de planification",
    otherError = "Erreur! Veuillez contacter le developpeur.",
    halfYearTimePeriod = "Six mois",
    categoryStatisticsTitle = "Statistiques par catégories",
    executedStatisticsTitle = "Statistiques d'exécution",
)

internal val brazilianPortugueseAnalyticsString = AnalyticsStrings(
    topAppBarTitle = "Análises",
    menuIconDesc = "Menu",
    timeTabTitle = "Horário",
    workLoadTabTitle = "Carga de trabalho",
    intelligenceTabTitle = "Informação",
    weekTimePeriod = "Semana",
    monthTimePeriod = "Mês",
    yearTimePeriod = "Ano",
    timeSelectorTitle = "Período de tempo:",
    refreshAnalyticIconDesc = "Atualizar análises",
    otherAnalyticsName = "Outras",
    allTimeTitle = "Total:",
    totalCountTaskTitle = "Número\ndas tarefas",
    averageCountTaskTitle = "Número\ndas tarefas por dia",
    totalTimeTaskTitle = "Tempo total\ndas tarefas",
    averageTimeTaskTitle = "Tempo médio\ndas tarefas",
    workLoadAnalyticsTitle = "Estatísticas da carga de trabalho",
    planningAnalyticsTitle = "Estatísticas do planejamento",
    otherError = "Erro! Contate o desenvolvedor.",
    halfYearTimePeriod = "Metade do ano",
    categoryStatisticsTitle = "Estatísticas da categoria",
    executedStatisticsTitle = "Estatísticas de execução",
)

internal val turkishAnalyticsString = AnalyticsStrings(
    topAppBarTitle = "Analiz",
    menuIconDesc = "Menü",
    timeTabTitle = "Zaman",
    workLoadTabTitle = "İş Yükü",
    intelligenceTabTitle = "Bilgi",
    weekTimePeriod = "Hafta",
    monthTimePeriod = "Ay",
    yearTimePeriod = "Yıl",
    timeSelectorTitle = "Zaman periyodu:",
    refreshAnalyticIconDesc = "Analizi yenile",
    otherAnalyticsName = "Diğer",
    allTimeTitle = "Toplam:",
    totalCountTaskTitle = "Görev\nsayısı",
    averageCountTaskTitle = "Günlük\nortalama görev sayısı",
    totalTimeTaskTitle = "Görevlerin\ntoplam süresi",
    averageTimeTaskTitle = "Görevlerin\nortalama süresi",
    workLoadAnalyticsTitle = "İş yükü istatistikleri",
    planningAnalyticsTitle = "Planlama istatistikleri",
    otherError = "Hata! Geliştirici ile iletişime geçin.",
    halfYearTimePeriod = "Yarım yıl",
    categoryStatisticsTitle = "Kategori istatistikleri",
    executedStatisticsTitle = "Yürütme istatistikleri",
)

internal val vietnameseAnalyticsString = AnalyticsStrings(
    topAppBarTitle = "Phân tích",
    menuIconDesc = "Thực đơn",
    timeTabTitle = "Thời gian",
    workLoadTabTitle = "Khối lượng công việc",
    intelligenceTabTitle = "Thông tin",
    weekTimePeriod = "Tuần",
    monthTimePeriod = "Tháng",
    yearTimePeriod = "Năm",
    timeSelectorTitle = "Khoảng thời gian:",
    refreshAnalyticIconDesc = "Làm mới phân tích",
    otherAnalyticsName = "Khác",
    allTimeTitle = "Tổng cộng:",
    totalCountTaskTitle = "Số\ncủa nhiệm vụ",
    averageCountTaskTitle = "Số\ncủa nhiệm vụ mỗi ngày",
    totalTimeTaskTitle = "Tổng thời gian\ncủa nhiệm vụ",
    averageTimeTaskTitle =	"Thời gian trung bình\ncủa nhiệm vụ",
    workLoadAnalyticsTitle = "Thống kê khối lượng công việc",
    planningAnalyticsTitle = "Thống kê kế hoạch",
    otherError = "Lỗi! Hãy liên hệ với nhà phát triển.",
    halfYearTimePeriod = "Nửa năm",
    categoryStatisticsTitle = "Thống kê danh mục",
    executedStatisticsTitle = "Thống kê sự tuân thủ",
)

internal val polishAnalyticsString = AnalyticsStrings(
    topAppBarTitle = "Analizy",
    menuIconDesc = "Menu",
    timeTabTitle = "Czas",
    workLoadTabTitle = "Obciążenie",
    intelligenceTabTitle = "Informacje",
    weekTimePeriod = "Tydzień",
    monthTimePeriod = "Miesiąc",
    yearTimePeriod = "Rok",
    timeSelectorTitle = "Okres czasu:",
    refreshAnalyticIconDesc = "Odśwież analizy",
    otherAnalyticsName = "Inne",
    allTimeTitle = "Wszystkie:",
    totalCountTaskTitle = "Zadań ogółem",
    averageCountTaskTitle = "Zadań dziennie",
    totalTimeTaskTitle = "Czas spędzony na zadaniach",
    averageTimeTaskTitle = "Średni czas na jedno zadanie",
    workLoadAnalyticsTitle = "Statystyki obciążenia",
    planningAnalyticsTitle = "Statystyki planowania",
    otherError = "Coś poszło nie tak. Skontaktuj się z twórcą aplikacji by otrzymać pomoc.",
    halfYearTimePeriod = "Pół roku",
    categoryStatisticsTitle = "Statystyki kategorii",
    executedStatisticsTitle = "Statystyki wykonania",
)

internal val italianAnalyticsString = AnalyticsStrings(
    topAppBarTitle = "Statistiche",
    menuIconDesc = "Menu",
    timeTabTitle = "Tempi",
    workLoadTabTitle = "Carico di Lavoro",
    intelligenceTabTitle = "Informazioni",
    weekTimePeriod = "Settimana",
    monthTimePeriod = "Mese",
    yearTimePeriod = "Anno",
    timeSelectorTitle = "Periodo di Tempo:",
    refreshAnalyticIconDesc = "Aggiorna Statistiche",
    otherAnalyticsName = "Altro",
    allTimeTitle = "Totale:",
    totalCountTaskTitle = "Task Totali",
    averageCountTaskTitle = "Task Giornalieri",
    totalTimeTaskTitle = "Tempo speso\nsui task",
    averageTimeTaskTitle = "Tempo medio\nper task",
    workLoadAnalyticsTitle = "Carico di Lavoro",
    planningAnalyticsTitle = "Pianificazione",
    otherError = "Qualcosa è andato storto. Contatta lo sviluppatore per ottenere assistenza.",
    halfYearTimePeriod = "Metà Anno",
    categoryStatisticsTitle = "Categorie",
    executedStatisticsTitle = "Completamento",
)

internal val LocalAnalyticsStrings = staticCompositionLocalOf<AnalyticsStrings> {
    error("Analytics Strings is not provided")
}

internal fun fetchAnalyticsStrings(language: TimePlannerLanguage) = when (language) {
    TimePlannerLanguage.EN -> englishAnalyticsString
    TimePlannerLanguage.RU -> russianAnalyticsString
    TimePlannerLanguage.DE -> germanAnalyticsString
    TimePlannerLanguage.ES -> spanishAnalyticsString
    TimePlannerLanguage.FA -> persianAnalyticsString
    TimePlannerLanguage.FR -> frenchAnalyticsString
    TimePlannerLanguage.PT_BR -> brazilianPortugueseAnalyticsString
    TimePlannerLanguage.TR -> turkishAnalyticsString
    TimePlannerLanguage.VN -> vietnameseAnalyticsString
    TimePlannerLanguage.PL -> polishAnalyticsString
    TimePlannerLanguage.IT -> italianAnalyticsString
}

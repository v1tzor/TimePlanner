# Timeline: алгоритм, инварианты и регрессии

Актуально для Home Timeline после ревизии 26.07.2026.

## 1. Источник истины

Канонический диапазон задачи хранится только в Room и приходит в интерфейс по цепочке:

```text
Room Flow
→ ScheduleRepository
→ ScheduleInteractor
→ ScheduleWorkProcessor
→ HomeAction.UpdateSchedule
→ HomeState.timelineSchedule
→ Timeline
```

Пиксели, положение карточки и незавершённый drag никогда не являются
постоянными данными.

Запись диапазона идёт в обратную сторону:

```text
drag завершён
→ TimelineTaskUpdateRequestUi
→ HomeEvent.UpdateTimelineTimeTask
→ TimelineWorkProcessor
→ TimelineInteractor.updateTimeTask
→ проверка duration и overlap
→ Room
```

У Timeline mutation отдельный `BackgroundKey.TIMELINE_MUTATION`. Обычные
действия Home не должны отменять coroutine записи диапазона.

## 2. Состояние pending mutation

После отпускания пальца `TimelineGestureState` создаёт запрос:

```text
operationId + timeTaskId + expected TimeRange
```

Локальная копия сразу удерживает preview, чтобы отпускание пальца и ближайшая
recomposition не дали ни одного кадра со старым диапазоном. В том же событии
запрос поднимается в `HomeState.pendingTimelineTaskUpdate` и живёт там отдельно
от `TimelineTab` и выбранной карточки.

Состояния операции:

```text
NONE
  └─ finish changed drag
       → WAITING_FOR_REPOSITORY
            ├─ Room Flow содержит expected TimeRange → ACK → NONE
            └─ interactor вернул failure          → REJECT → NONE
```

Правила:

1. `exitEditMode()` очищает selection и активный drag, но не pending request.
2. Повторный вход показывает ожидаемый диапазон, а не старый snapshot.
3. Новый drag и остальные Home-события, способные уйти с экрана или записать
   stale-модель задачи, блокируются, пока Room Flow не подтвердит диапазон или
   запись не завершится ошибкой.
4. ACK выполняется только при совпадении `timeTaskId` и полного `TimeRange`.
5. REJECT выполняется только для совпадающего `operationId`, поэтому старый
   результат не может откатить более новую операцию.
6. При REJECT preview заменяется каноническим диапазоном из последней модели.
7. Pending и failure receipt в `HomeState` помечены `@Transient`: они
   описывают только незавершённую операцию и не становятся источником
   канонических данных после восстановления процесса.
8. Новый экземпляр `TimelineTab` получает pending из Store, сразу показывает
   expected range и не может начать gesture из старого snapshot.
9. ACK отправляется только после того, как `HomeState.timelineSchedule`,
   собранный из Room Flow, содержит expected range.

Это устраняет последовательность:

```text
08:30–15:45
→ resize до 08:30–08:35
→ exit
→ немедленный MOVE
→ ошибочное восстановление 15:45
```

## 3. Контракт времени

Timeline использует полуоткрытые интервалы:

```text
[from, to)
```

Следствия:

- минимальный шаг и минимальная duration равны 5 минутам;
- соседние задачи могут иметь `current.to == next.from`;
- `dayTimeRange.to` — следующее `00:00`, а не `23:59`;
- задача `23:55–00:00` полностью принадлежит исходному дню;
- пересечение overnight-задачи с экраном дня выполняется только для layout и
  подписей; при сохранении используется полный абсолютный `TimeRange`.

## 4. Масштаб Timeline

`TimelineLayout` строит один упорядоченный набор `TimelineScaleSegment`.

Каждый segment содержит:

```text
TimeRange ↔ [topPx, bottomPx]
```

Внутри segment преобразование линейное:

```text
progress = (time - from) / (to - from)
offset = top + (bottom - top) × progress
```

Обратное преобразование использует тот же segment:

```text
progress = (offset - top) / (bottom - top)
time = from + (to - from) × progress
```

Высота занятых и свободных промежутков может отличаться:

- короткая задача получает пропорциональную высоту, но не меньше 58 dp;
- длинная часть задачи сжимается;
- свободное время использует отдельный коэффициент;
- очень длинная задача ограничивается максимальной высотой.

Из-за этого нельзя переводить delta через «средние пиксели в час». Каждая
операция использует только `TimelineScale.fetchOffset()` и
`TimelineScale.fetchTime()`.

## 5. Замороженный scale

При входе в edit mode сохраняется текущий `TimelineLayoutResult`.

До выхода из edit mode один и тот же scale используется для:

- линий времени;
- подписей;
- preview диапазона;
- прямого `time → px`;
- обратного `px → time`;
- drag;
- auto-scroll.

Scale не пересчитывается на каждом движении пальца. Это основной инвариант
против изменения чувствительности, скачков и дрожания.

Frozen snapshot хранит пары `(taskId, visibleTimeRange)`. Подтверждённое Room
изменение диапазона выбранной задачи обновляет ключ snapshot, но не меняет
замороженную scale: пользователь не получает скачок сразу после отпускания
пальца. Если Room добавил, удалил или изменил другую задачу, snapshot больше не
используется. Edit session безопасно закрывается и Timeline переходит на новый
полный layout без смешивания старой scale с новыми constraints и без
`checkNotNull`-падения.

При смене scale сохраняется время в центре viewport:

```text
anchorTime = oldScale.fetchTime(scroll + viewport / 2)
newScroll = newScale.fetchOffset(anchorTime) - viewport / 2
```

Поэтому после resize длинной или full-day задачи пользователь остаётся возле
того же времени, а не переносится в другую часть дня.

## 6. Временная и визуальная геометрия

Канонические временные offsets всегда вычисляются из preview:

```text
startY = scale.fetchOffset(preview.from)
endY = scale.fetchOffset(preview.to)
```

Подписи слева используют именно `startY` и `endY`, а не высоту карточки.
После измерения все часовые и граничные подписи проходят через одну
chronological layout-процедуру. Она сортирует anchor по времени, сохраняет
минимальный зазор 2 dp и при необходимости сдвигает группу от верхнего или
нижнего края. Поэтому для duration 5 минут начало всегда остаётся выше конца,
а тексты не накладываются друг на друга.

Минимальная высота временного slot равна 58 dp; сама карточка оставляет внутри
slot 2 dp межзадачного интервала. Даже когда пять минут в текущем сжатом segment
занимают несколько пикселей, visual card остаётся читаемой. Дополнительная
визуальная высота:

- не меняет `TimeRange`;
- не участвует в `px → time`;
- не меняет duration при MOVE;
- не используется как auto-scroll anchor.

При MOVE высота фиксируется на старте gesture по текущей фактически
отрисованной позиции карточки, а не по исходной позиции frozen layout. Поэтому
последовательность `RESIZE → MOVE` использует уже изменённые обе границы и при
этом высота не морфится при переходе между segment с разным масштабом. При
`RESIZE_START` за временем непрерывно следует верхняя визуальная граница, при
`RESIZE_END` — нижняя. Верхний padding равен минимальной высоте slot, поэтому
короткая задача у `00:00` не уходит в отрицательные координаты.

Высота scroll content расширяется, если короткая выбранная карточка около
`00:00` выходит ниже последней временной линии. Поэтому её control и граница
остаются достижимыми.

## 7. Gesture session

Каждый drag получает уникальный `sessionId`.

На старте фиксируются:

- task ID;
- drag mode;
- исходный полный `TimeRange`;
- предыдущий drag mode;
- scale текущего edit session;
- ограничения соседей и дня, переданные текущей моделью.

Raw pixel accumulator хранится как обычное поле, а не Compose state. Compose
получает новое observable значение только после изменения snapped
`TimeRange`. Движение на каждый отдельный пиксель не вызывает полный
recomposition Timeline.

Если drag не изменил время, `finishTaskEdit()`:

- не создаёт mutation;
- восстанавливает предыдущий mode;
- не переводит карточку в другую геометрию.

Cancel всегда восстанавливает исходный диапазон текущего gesture.

MOVE и RESIZE распознаются дочерним pointer handler сразу после down и
потребляют жест до родительского `verticalScroll`. Это исключает случайный
переход одного и того же движения от карточки к прокрутке Timeline.

## 8. MOVE

Duration фиксируется на старте:

```text
duration = original.to - original.from
```

Желаемое начало:

```text
desiredOffset = offset(original.from) + accumulatedDelta
desiredStart = snap(scale.fetchTime(desiredOffset), 5 min)
```

Допустимые начала строятся из:

- текущего промежутка между соседями;
- свободных промежутков, в которые целиком помещается `duration`.

Затем выбирается ближайшее допустимое начало:

```text
result.from = nearestAllowed(desiredStart)
result.to = result.from + duration
```

Duration никогда не вычисляется по высоте карточки.

Внешний overshoot насыщается по минимальному и максимальному допустимому
offset. Если палец ушёл далеко выше `00:00` или ниже конца дня, один обратный
шаг сразу двигает задачу. Невидимого «холостого» обратного пути нет.

Внутренние занятые gaps не насыщаются: задача может корректно перепрыгнуть к
ближайшему свободному диапазону.

## 9. RESIZE

Для верхней границы:

```text
newFrom = snap(scale.fetchTime(offset(original.from) + delta), 5 min)
newFrom ∈ [minimumStartTime, original.to - minimumDuration]
```

Для нижней границы:

```text
newTo = snap(scale.fetchTime(offset(original.to) + delta), 5 min)
newTo ∈ [original.from + minimumDuration, maximumEndTime]
```

Противоположная граница всегда остаётся фиксированной.

Overshoot насыщается на границе допустимого диапазона. После сильного выхода
за соседнюю задачу, минимум duration или край дня обратное движение сразу
возвращает изменение.

## 10. Auto-scroll

Auto-scroll работает в одной `ScrollState.scroll(MutatePriority.UserInput)`
сессии на один `sessionId`.

Скорость задаётся в dp/сек, а не dp/frame:

```text
step = maximumSpeed × edgePressure × elapsedSeconds
```

`elapsedSeconds` ограничен 50 ms, чтобы первый кадр после паузы не дал большой
скачок. Поэтому одинаковая секунда жеста даёт одинаковое перемещение на
60, 90 и 120 Hz.

Используется только реально consumed значение:

```text
consumed = ScrollScope.scrollBy(requested)
```

Content-coordinate pointer anchor на старте берётся из реально отрисованной
геометрии control:

```text
MOVE         → card.top + card.height / 2
RESIZE_START → card.top
RESIZE_END   → card.bottom
```

Во время обычного drag он непрерывно получает raw pointer delta и не зависит от
5-минутного snap. Во время auto-scroll он обновляется реально consumed
значением:

```text
contentAnchor += consumed
gestureDelta += consumed
```

Инвариант неподвижного пальца:

```text
(contentAnchor + consumed) - (scroll + consumed)
    == contentAnchor - scroll
```

Поэтому auto-scroll не затухает сам и не начинает дёргаться возле края.

Поэтому snap меняет время и подпись только на границе шага, но не меняет
edge-pressure и не создаёт скачок auto-scroll. В viewport меньше удвоенной
edge-зоны её эффективный размер ограничивается половиной viewport, поэтому
верхнее и нижнее направления не перекрываются.

## 11. Актуальные callback в `pointerInput`

Coroutine внутри `pointerInput(key)` перезапускается только при изменении key.
Task ID, `enabled` и drag mode обычно остаются прежними после Room emission.

Поэтому все callback, используемые внутри долгоживущего `pointerInput`, читаются
через `rememberUpdatedState`.

Нельзя добавлять весь mutable task model в key: обновление модели во время
активного drag отменило бы gesture. `rememberUpdatedState` сохраняет gesture
lifecycle и одновременно даёт последнюю модель.

Именно старое callback-замыкание было одной из причин восстановления границы
`15:45` после уже сохранённого resize.

## 12. Края и специальные задачи

### Пять минут

- persisted duration остаётся 5 минут;
- MOVE сохраняет её точно;
- карточка не схлопывается ниже читаемой высоты;
- новый gesture не стартует из старого диапазона.

### Весь день

Точный диапазон:

```text
[dayStart, nextDayStart)
```

У него нет другой допустимой позиции, поэтому `canMove == false`. Обе resize
границы остаются доступны.

### Overnight

- full range остаётся абсолютным и единым;
- layout текущего дня использует пересечение с `dayTimeRange`;
- overlay не дублирует задачу;
- resize обязан оставлять на текущем экране минимум один шаг 5 минут: source
  не может начать позже `23:55`, overlay не может закончиться раньше `00:05`;
- если `canMove == false`, но разрешён resize одной из границ, кнопка всё равно
  позволяет войти в edit mode; сам MOVE gesture остаётся выключенным.

### DST

Часовые метки строятся последовательным календарным добавлением часа до
точного `dayTimeRange.to`.

- весенний 23-часовой день не получает лишнюю метку;
- осенний 25-часовой день не теряет повторяющийся час;
- последняя метка всегда равна следующему `00:00`.

## 13. Touch-зоны границ

Видимая полоска resize имеет высоту 4 dp, её layout-зона — 12 dp.
Допустимая область начала drag расширена ещё на 18 dp с каждой стороны. Вместе
с минимальной touch-областью Compose это позволяет использовать полную зону
около 48 dp, а не обрезанные прежним фильтром 36 dp.

Расширение сделано симметрично для верхней и нижней границы. Оно достаточно
для уверенного захвата короткой задачи и не создаёт полного перекрытия двух
resize-зон на минимальной карточке.

## 14. Регрессионная матрица

Обязательные проверки:

| Сценарий | Инвариант |
|---|---|
| `08:30–15:45 → 08:30–08:35 → exit → MOVE` | duration остаётся 5 минут |
| Повторный MOVE до Room emission | gesture ждёт ACK и не пишет старый range |
| Пересоздание TimelineTab до ACK | pending восстанавливается из HomeState |
| Попытка уйти в Agenda до ACK | stale full-upsert не запускается |
| Ошибка update | preview возвращается к repository range |
| Повторная recomposition с тем же pointer key | drag callback использует новую модель |
| Несколько sub-step delta | они суммируются до одного шага 5 минут |
| Overshoot и один шаг назад | время меняется сразу |
| MOVE через занятый gap | выбирается ближайший допустимый gap |
| `00:00–00:05` | верхняя граница достижима |
| `23:55–00:00` | нижняя граница достижима |
| exact full-day | MOVE выключен, resize доступен |
| overnight source/overlay | на экране остаётся минимум один шаг 5 минут |
| add/delete/change во время edit | frozen layout не смешивает snapshots и не падает |
| ACK диапазона выбранной задачи | frozen scale не меняется и viewport не прыгает |
| `RESIZE → MOVE` без выхода из edit mode | MOVE использует новый range и текущую высоту |
| короткая duration | start label выше end label, между текстами есть зазор |
| MOVE/RESIZE внутри verticalScroll | drag остаётся у задачи, родитель не перехватывает |
| auto-scroll 60/120 Hz | одинаковая дистанция за одинаковое время |
| auto-scroll в низком viewport | верхняя и нижняя edge-зоны не перекрываются |
| spring/fall DST | точный day end и корректное число часовых интервалов |

## 15. Основные файлы

- `TimelineGrid.kt` — единая композиция scale, layout, labels и auto-scroll.
- `TimelineLayout.kt` — построение segments и позиций.
- `TimelineScale.kt` — взаимно согласованные `time ↔ px`.
- `TimelineGestureState.kt` — gesture session, snap, clamp и локальный preview.
- `TimelineTaskCard.kt` — visual card, MOVE и resize pointer handlers.
- `TimelineInteractor.kt` — day clipping, соседние ограничения и validation.
- `TimelineWorkProcessor.kt` — последовательная запись и failure receipt.
- `HomeComposeStore.kt` — lifecycle pending mutation и защита от stale Home events.

## 16. Официальная документация

- [`pointerInput` keys и `rememberUpdatedState`](https://developer.android.com/reference/kotlin/androidx/compose/ui/input/pointer/pointerInput.modifier)
- [Pointer input и gesture lifecycle](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/understand-gestures)
- [Drag, swipe and fling](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/drag-swipe-fling)
- [State и single source of truth](https://developer.android.com/develop/ui/compose/state)
- [State hoisting](https://developer.android.com/develop/ui/compose/state-hoisting)
- [`LaunchedEffect` keys и side effects](https://developer.android.com/develop/ui/compose/side-effects)
- [`ScrollableState` и consumed delta](https://developer.android.com/reference/kotlin/androidx/compose/foundation/gestures/ScrollableState)
- [Custom layouts](https://developer.android.com/develop/ui/compose/layouts/custom)
- [Compose performance](https://developer.android.com/develop/ui/compose/performance/bestpractices)
- [Minimum touch targets](https://developer.android.com/develop/ui/compose/accessibility/api-defaults)

package ru.aleshin.features.overview.impl.domain.interactors;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.domain.common.RecurringScheduleManager;
import ru.aleshin.core.domain.common.ScheduleStatusChecker;
import ru.aleshin.core.domain.common.TimeTaskStatusChecker;
import ru.aleshin.core.domain.repository.ScheduleRepository;
import ru.aleshin.core.domain.repository.TimeTaskRepository;
import ru.aleshin.core.utils.managers.DateManager;
import ru.aleshin.features.overview.impl.domain.common.OverviewEitherWrapper;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ScheduleInteractor_Base_Factory implements Factory<ScheduleInteractor.Base> {
  private final Provider<ScheduleRepository> scheduleRepositoryProvider;

  private final Provider<TimeTaskRepository> timeTaskRepositoryProvider;

  private final Provider<RecurringScheduleManager> recurringScheduleManagerProvider;

  private final Provider<ScheduleStatusChecker> scheduleStatusCheckerProvider;

  private final Provider<TimeTaskStatusChecker> timeTaskStatusCheckerProvider;

  private final Provider<DateManager> dateManagerProvider;

  private final Provider<OverviewEitherWrapper> eitherWrapperProvider;

  private ScheduleInteractor_Base_Factory(Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<TimeTaskRepository> timeTaskRepositoryProvider,
      Provider<RecurringScheduleManager> recurringScheduleManagerProvider,
      Provider<ScheduleStatusChecker> scheduleStatusCheckerProvider,
      Provider<TimeTaskStatusChecker> timeTaskStatusCheckerProvider,
      Provider<DateManager> dateManagerProvider,
      Provider<OverviewEitherWrapper> eitherWrapperProvider) {
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;
    this.timeTaskRepositoryProvider = timeTaskRepositoryProvider;
    this.recurringScheduleManagerProvider = recurringScheduleManagerProvider;
    this.scheduleStatusCheckerProvider = scheduleStatusCheckerProvider;
    this.timeTaskStatusCheckerProvider = timeTaskStatusCheckerProvider;
    this.dateManagerProvider = dateManagerProvider;
    this.eitherWrapperProvider = eitherWrapperProvider;
  }

  @Override
  public ScheduleInteractor.Base get() {
    return newInstance(scheduleRepositoryProvider.get(), timeTaskRepositoryProvider.get(), recurringScheduleManagerProvider.get(), scheduleStatusCheckerProvider.get(), timeTaskStatusCheckerProvider.get(), dateManagerProvider.get(), eitherWrapperProvider.get());
  }

  public static ScheduleInteractor_Base_Factory create(
      Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<TimeTaskRepository> timeTaskRepositoryProvider,
      Provider<RecurringScheduleManager> recurringScheduleManagerProvider,
      Provider<ScheduleStatusChecker> scheduleStatusCheckerProvider,
      Provider<TimeTaskStatusChecker> timeTaskStatusCheckerProvider,
      Provider<DateManager> dateManagerProvider,
      Provider<OverviewEitherWrapper> eitherWrapperProvider) {
    return new ScheduleInteractor_Base_Factory(scheduleRepositoryProvider, timeTaskRepositoryProvider, recurringScheduleManagerProvider, scheduleStatusCheckerProvider, timeTaskStatusCheckerProvider, dateManagerProvider, eitherWrapperProvider);
  }

  public static ScheduleInteractor.Base newInstance(ScheduleRepository scheduleRepository,
      TimeTaskRepository timeTaskRepository, RecurringScheduleManager recurringScheduleManager,
      ScheduleStatusChecker scheduleStatusChecker, TimeTaskStatusChecker timeTaskStatusChecker,
      DateManager dateManager, OverviewEitherWrapper eitherWrapper) {
    return new ScheduleInteractor.Base(scheduleRepository, timeTaskRepository, recurringScheduleManager, scheduleStatusChecker, timeTaskStatusChecker, dateManager, eitherWrapper);
  }
}

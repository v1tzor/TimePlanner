package ru.aleshin.features.templates.impl.domain.interactors;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.domain.common.TimeOverlayManager;
import ru.aleshin.core.domain.repository.ScheduleRepository;
import ru.aleshin.core.domain.repository.TimeTaskRepository;
import ru.aleshin.core.utils.managers.DateManager;
import ru.aleshin.features.templates.impl.domain.common.HomeEitherWrapper;

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
public final class RepeatTaskInteractor_Base_Factory implements Factory<RepeatTaskInteractor.Base> {
  private final Provider<TimeTaskRepository> timeTaskRepositoryProvider;

  private final Provider<ScheduleRepository> scheduleRepositoryProvider;

  private final Provider<HomeEitherWrapper> eitherWrapperProvider;

  private final Provider<TimeOverlayManager> overlayManagerProvider;

  private final Provider<DateManager> dateManagerProvider;

  private RepeatTaskInteractor_Base_Factory(Provider<TimeTaskRepository> timeTaskRepositoryProvider,
      Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<HomeEitherWrapper> eitherWrapperProvider,
      Provider<TimeOverlayManager> overlayManagerProvider,
      Provider<DateManager> dateManagerProvider) {
    this.timeTaskRepositoryProvider = timeTaskRepositoryProvider;
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;
    this.eitherWrapperProvider = eitherWrapperProvider;
    this.overlayManagerProvider = overlayManagerProvider;
    this.dateManagerProvider = dateManagerProvider;
  }

  @Override
  public RepeatTaskInteractor.Base get() {
    return newInstance(timeTaskRepositoryProvider.get(), scheduleRepositoryProvider.get(), eitherWrapperProvider.get(), overlayManagerProvider.get(), dateManagerProvider.get());
  }

  public static RepeatTaskInteractor_Base_Factory create(
      Provider<TimeTaskRepository> timeTaskRepositoryProvider,
      Provider<ScheduleRepository> scheduleRepositoryProvider,
      Provider<HomeEitherWrapper> eitherWrapperProvider,
      Provider<TimeOverlayManager> overlayManagerProvider,
      Provider<DateManager> dateManagerProvider) {
    return new RepeatTaskInteractor_Base_Factory(timeTaskRepositoryProvider, scheduleRepositoryProvider, eitherWrapperProvider, overlayManagerProvider, dateManagerProvider);
  }

  public static RepeatTaskInteractor.Base newInstance(TimeTaskRepository timeTaskRepository,
      ScheduleRepository scheduleRepository, HomeEitherWrapper eitherWrapper,
      TimeOverlayManager overlayManager, DateManager dateManager) {
    return new RepeatTaskInteractor.Base(timeTaskRepository, scheduleRepository, eitherWrapper, overlayManager, dateManager);
  }
}

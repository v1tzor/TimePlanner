package ru.aleshin.features.overview.impl.presentation.ui.details.store;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.utils.managers.DateManager;
import ru.aleshin.features.overview.impl.domain.interactors.ScheduleInteractor;

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
public final class DetailsWorkProcessor_Base_Factory implements Factory<DetailsWorkProcessor.Base> {
  private final Provider<ScheduleInteractor> scheduleInteractorProvider;

  private final Provider<DateManager> dateManagerProvider;

  private DetailsWorkProcessor_Base_Factory(Provider<ScheduleInteractor> scheduleInteractorProvider,
      Provider<DateManager> dateManagerProvider) {
    this.scheduleInteractorProvider = scheduleInteractorProvider;
    this.dateManagerProvider = dateManagerProvider;
  }

  @Override
  public DetailsWorkProcessor.Base get() {
    return newInstance(scheduleInteractorProvider.get(), dateManagerProvider.get());
  }

  public static DetailsWorkProcessor_Base_Factory create(
      Provider<ScheduleInteractor> scheduleInteractorProvider,
      Provider<DateManager> dateManagerProvider) {
    return new DetailsWorkProcessor_Base_Factory(scheduleInteractorProvider, dateManagerProvider);
  }

  public static DetailsWorkProcessor.Base newInstance(ScheduleInteractor scheduleInteractor,
      DateManager dateManager) {
    return new DetailsWorkProcessor.Base(scheduleInteractor, dateManager);
  }
}

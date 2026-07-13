package ru.aleshin.features.overview.impl.presentation.ui.overview.store;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.utils.managers.DateManager;
import ru.aleshin.features.overview.impl.domain.interactors.MainCategoriesInteractor;
import ru.aleshin.features.overview.impl.domain.interactors.ScheduleInteractor;
import ru.aleshin.features.overview.impl.domain.interactors.ShareTextInteractor;
import ru.aleshin.features.overview.impl.domain.interactors.UndefinedTasksInteractor;

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
public final class OverviewWorkProcessor_Base_Factory implements Factory<OverviewWorkProcessor.Base> {
  private final Provider<ScheduleInteractor> scheduleInteractorProvider;

  private final Provider<MainCategoriesInteractor> categoriesInteractorProvider;

  private final Provider<UndefinedTasksInteractor> undefinedTasksInteractorProvider;

  private final Provider<ShareTextInteractor> shareTextInteractorProvider;

  private final Provider<DateManager> dateManagerProvider;

  private OverviewWorkProcessor_Base_Factory(
      Provider<ScheduleInteractor> scheduleInteractorProvider,
      Provider<MainCategoriesInteractor> categoriesInteractorProvider,
      Provider<UndefinedTasksInteractor> undefinedTasksInteractorProvider,
      Provider<ShareTextInteractor> shareTextInteractorProvider,
      Provider<DateManager> dateManagerProvider) {
    this.scheduleInteractorProvider = scheduleInteractorProvider;
    this.categoriesInteractorProvider = categoriesInteractorProvider;
    this.undefinedTasksInteractorProvider = undefinedTasksInteractorProvider;
    this.shareTextInteractorProvider = shareTextInteractorProvider;
    this.dateManagerProvider = dateManagerProvider;
  }

  @Override
  public OverviewWorkProcessor.Base get() {
    return newInstance(scheduleInteractorProvider.get(), categoriesInteractorProvider.get(), undefinedTasksInteractorProvider.get(), shareTextInteractorProvider.get(), dateManagerProvider.get());
  }

  public static OverviewWorkProcessor_Base_Factory create(
      Provider<ScheduleInteractor> scheduleInteractorProvider,
      Provider<MainCategoriesInteractor> categoriesInteractorProvider,
      Provider<UndefinedTasksInteractor> undefinedTasksInteractorProvider,
      Provider<ShareTextInteractor> shareTextInteractorProvider,
      Provider<DateManager> dateManagerProvider) {
    return new OverviewWorkProcessor_Base_Factory(scheduleInteractorProvider, categoriesInteractorProvider, undefinedTasksInteractorProvider, shareTextInteractorProvider, dateManagerProvider);
  }

  public static OverviewWorkProcessor.Base newInstance(ScheduleInteractor scheduleInteractor,
      MainCategoriesInteractor categoriesInteractor,
      UndefinedTasksInteractor undefinedTasksInteractor, ShareTextInteractor shareTextInteractor,
      DateManager dateManager) {
    return new OverviewWorkProcessor.Base(scheduleInteractor, categoriesInteractor, undefinedTasksInteractor, shareTextInteractor, dateManager);
  }
}

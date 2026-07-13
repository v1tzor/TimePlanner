package ru.aleshin.features.templates.impl.presentation.ui.templates.store;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.presentation.notifications.TemplatesAlarmManager;
import ru.aleshin.core.presentation.notifications.TimeTaskAlarmManager;
import ru.aleshin.features.templates.impl.domain.interactors.MainCategoriesInteractor;
import ru.aleshin.features.templates.impl.domain.interactors.RepeatTaskInteractor;
import ru.aleshin.features.templates.impl.domain.interactors.TemplatesInteractor;

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
public final class TemplatesWorkProcessor_Base_Factory implements Factory<TemplatesWorkProcessor.Base> {
  private final Provider<TemplatesInteractor> templatesInteractorProvider;

  private final Provider<RepeatTaskInteractor> repeatTaskInteractorProvider;

  private final Provider<MainCategoriesInteractor> categoriesInteractorProvider;

  private final Provider<TimeTaskAlarmManager> timeTaskAlarmManagerProvider;

  private final Provider<TemplatesAlarmManager> templatesAlarmManagerProvider;

  private TemplatesWorkProcessor_Base_Factory(
      Provider<TemplatesInteractor> templatesInteractorProvider,
      Provider<RepeatTaskInteractor> repeatTaskInteractorProvider,
      Provider<MainCategoriesInteractor> categoriesInteractorProvider,
      Provider<TimeTaskAlarmManager> timeTaskAlarmManagerProvider,
      Provider<TemplatesAlarmManager> templatesAlarmManagerProvider) {
    this.templatesInteractorProvider = templatesInteractorProvider;
    this.repeatTaskInteractorProvider = repeatTaskInteractorProvider;
    this.categoriesInteractorProvider = categoriesInteractorProvider;
    this.timeTaskAlarmManagerProvider = timeTaskAlarmManagerProvider;
    this.templatesAlarmManagerProvider = templatesAlarmManagerProvider;
  }

  @Override
  public TemplatesWorkProcessor.Base get() {
    return newInstance(templatesInteractorProvider.get(), repeatTaskInteractorProvider.get(), categoriesInteractorProvider.get(), timeTaskAlarmManagerProvider.get(), templatesAlarmManagerProvider.get());
  }

  public static TemplatesWorkProcessor_Base_Factory create(
      Provider<TemplatesInteractor> templatesInteractorProvider,
      Provider<RepeatTaskInteractor> repeatTaskInteractorProvider,
      Provider<MainCategoriesInteractor> categoriesInteractorProvider,
      Provider<TimeTaskAlarmManager> timeTaskAlarmManagerProvider,
      Provider<TemplatesAlarmManager> templatesAlarmManagerProvider) {
    return new TemplatesWorkProcessor_Base_Factory(templatesInteractorProvider, repeatTaskInteractorProvider, categoriesInteractorProvider, timeTaskAlarmManagerProvider, templatesAlarmManagerProvider);
  }

  public static TemplatesWorkProcessor.Base newInstance(TemplatesInteractor templatesInteractor,
      RepeatTaskInteractor repeatTaskInteractor, MainCategoriesInteractor categoriesInteractor,
      TimeTaskAlarmManager timeTaskAlarmManager, TemplatesAlarmManager templatesAlarmManager) {
    return new TemplatesWorkProcessor.Base(templatesInteractor, repeatTaskInteractor, categoriesInteractor, timeTaskAlarmManager, templatesAlarmManager);
  }
}

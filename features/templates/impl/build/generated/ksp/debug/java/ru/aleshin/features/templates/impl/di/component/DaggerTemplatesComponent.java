package ru.aleshin.features.templates.impl.di.component;

import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import javax.annotation.processing.Generated;
import ru.aleshin.core.domain.common.TimeOverlayManager;
import ru.aleshin.core.domain.repository.MainCategoryRepository;
import ru.aleshin.core.domain.repository.ScheduleRepository;
import ru.aleshin.core.domain.repository.TemplatesRepository;
import ru.aleshin.core.domain.repository.TimeTaskRepository;
import ru.aleshin.core.presentation.notifications.TemplatesAlarmManager;
import ru.aleshin.core.presentation.notifications.TimeTaskAlarmManager;
import ru.aleshin.core.utils.managers.CoroutineManager;
import ru.aleshin.core.utils.managers.DateManager;
import ru.aleshin.features.templates.api.TemplatesContentProviderFactory;
import ru.aleshin.features.templates.impl.di.TemplatesFeatureDependencies;
import ru.aleshin.features.templates.impl.domain.common.HomeEitherWrapper;
import ru.aleshin.features.templates.impl.domain.common.HomeEitherWrapper_Base_Factory;
import ru.aleshin.features.templates.impl.domain.common.HomeErrorHandler_Base_Factory;
import ru.aleshin.features.templates.impl.domain.interactors.MainCategoriesInteractor;
import ru.aleshin.features.templates.impl.domain.interactors.MainCategoriesInteractor_Base_Factory;
import ru.aleshin.features.templates.impl.domain.interactors.RepeatTaskInteractor;
import ru.aleshin.features.templates.impl.domain.interactors.RepeatTaskInteractor_Base_Factory;
import ru.aleshin.features.templates.impl.domain.interactors.TemplatesInteractor;
import ru.aleshin.features.templates.impl.domain.interactors.TemplatesInteractor_Base_Factory;
import ru.aleshin.features.templates.impl.navigation.DefaultTemplatesContentProviderFactory;
import ru.aleshin.features.templates.impl.navigation.DefaultTemplatesContentProviderFactory_Factory;
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesComposeStore;
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesComposeStore_Factory_Factory;
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesWorkProcessor;
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesWorkProcessor_Base_Factory;

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
public final class DaggerTemplatesComponent {
  private DaggerTemplatesComponent() {
  }

  public static TemplatesComponent.Builder builder() {
    return new Builder();
  }

  private static final class Builder implements TemplatesComponent.Builder {
    private TemplatesFeatureDependencies templatesFeatureDependencies;

    @Override
    public Builder dependencies(TemplatesFeatureDependencies value) {
      this.templatesFeatureDependencies = Preconditions.checkNotNull(value);
      return this;
    }

    @Override
    public TemplatesComponent build() {
      Preconditions.checkBuilderRequirement(templatesFeatureDependencies, TemplatesFeatureDependencies.class);
      return new TemplatesComponentImpl(templatesFeatureDependencies);
    }
  }

  private static final class TemplatesComponentImpl implements TemplatesComponent {
    private final TemplatesComponentImpl templatesComponentImpl = this;

    Provider<TemplatesRepository> getTemplatesRepositoryProvider;

    Provider<HomeEitherWrapper.Base> baseProvider;

    Provider<TemplatesInteractor.Base> baseProvider2;

    Provider<TimeTaskRepository> getTimeTaskRepositoryProvider;

    Provider<ScheduleRepository> getSchedulesRepositoryProvider;

    Provider<TimeOverlayManager> getTimeOverlayManagerProvider;

    Provider<DateManager> getDateMangerProvider;

    Provider<RepeatTaskInteractor.Base> baseProvider3;

    Provider<MainCategoryRepository> getMainCategoryRepositoryProvider;

    Provider<MainCategoriesInteractor.Base> baseProvider4;

    Provider<TimeTaskAlarmManager> getTimeTaskAlarmManagerProvider;

    Provider<TemplatesAlarmManager> getTemplatesAlarmManagerProvider;

    Provider<TemplatesWorkProcessor.Base> baseProvider5;

    Provider<TemplatesWorkProcessor> bindProcessorProvider;

    Provider<CoroutineManager> getCoroutineManagerProvider;

    Provider<TemplatesComposeStore.Factory> factoryProvider;

    Provider<DefaultTemplatesContentProviderFactory> defaultTemplatesContentProviderFactoryProvider;

    Provider<TemplatesContentProviderFactory> bindContentProviderFactoryProvider;

    TemplatesComponentImpl(TemplatesFeatureDependencies templatesFeatureDependenciesParam) {

      initialize(templatesFeatureDependenciesParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final TemplatesFeatureDependencies templatesFeatureDependenciesParam) {
      this.getTemplatesRepositoryProvider = new GetTemplatesRepositoryProvider(templatesFeatureDependenciesParam);
      this.baseProvider = HomeEitherWrapper_Base_Factory.create(((Provider) (HomeErrorHandler_Base_Factory.create())));
      this.baseProvider2 = TemplatesInteractor_Base_Factory.create(getTemplatesRepositoryProvider, ((Provider) (baseProvider)));
      this.getTimeTaskRepositoryProvider = new GetTimeTaskRepositoryProvider(templatesFeatureDependenciesParam);
      this.getSchedulesRepositoryProvider = new GetSchedulesRepositoryProvider(templatesFeatureDependenciesParam);
      this.getTimeOverlayManagerProvider = new GetTimeOverlayManagerProvider(templatesFeatureDependenciesParam);
      this.getDateMangerProvider = new GetDateMangerProvider(templatesFeatureDependenciesParam);
      this.baseProvider3 = RepeatTaskInteractor_Base_Factory.create(getTimeTaskRepositoryProvider, getSchedulesRepositoryProvider, ((Provider) (baseProvider)), getTimeOverlayManagerProvider, getDateMangerProvider);
      this.getMainCategoryRepositoryProvider = new GetMainCategoryRepositoryProvider(templatesFeatureDependenciesParam);
      this.baseProvider4 = MainCategoriesInteractor_Base_Factory.create(getMainCategoryRepositoryProvider, ((Provider) (baseProvider)));
      this.getTimeTaskAlarmManagerProvider = new GetTimeTaskAlarmManagerProvider(templatesFeatureDependenciesParam);
      this.getTemplatesAlarmManagerProvider = new GetTemplatesAlarmManagerProvider(templatesFeatureDependenciesParam);
      this.baseProvider5 = TemplatesWorkProcessor_Base_Factory.create(((Provider) (baseProvider2)), ((Provider) (baseProvider3)), ((Provider) (baseProvider4)), getTimeTaskAlarmManagerProvider, getTemplatesAlarmManagerProvider);
      this.bindProcessorProvider = DoubleCheck.provider((Provider) (baseProvider5));
      this.getCoroutineManagerProvider = new GetCoroutineManagerProvider(templatesFeatureDependenciesParam);
      this.factoryProvider = TemplatesComposeStore_Factory_Factory.create(bindProcessorProvider, getCoroutineManagerProvider);
      this.defaultTemplatesContentProviderFactoryProvider = DefaultTemplatesContentProviderFactory_Factory.create(factoryProvider);
      this.bindContentProviderFactoryProvider = DoubleCheck.provider((Provider) (defaultTemplatesContentProviderFactoryProvider));
    }

    @Override
    public TemplatesContentProviderFactory contentProviderFactory() {
      return bindContentProviderFactoryProvider.get();
    }

    private static final class GetTemplatesRepositoryProvider implements Provider<TemplatesRepository> {
      private final TemplatesFeatureDependencies templatesFeatureDependencies;

      GetTemplatesRepositoryProvider(TemplatesFeatureDependencies templatesFeatureDependencies) {
        this.templatesFeatureDependencies = templatesFeatureDependencies;
      }

      @Override
      public TemplatesRepository get() {
        return Preconditions.checkNotNullFromComponent(templatesFeatureDependencies.getTemplatesRepository());
      }
    }

    private static final class GetTimeTaskRepositoryProvider implements Provider<TimeTaskRepository> {
      private final TemplatesFeatureDependencies templatesFeatureDependencies;

      GetTimeTaskRepositoryProvider(TemplatesFeatureDependencies templatesFeatureDependencies) {
        this.templatesFeatureDependencies = templatesFeatureDependencies;
      }

      @Override
      public TimeTaskRepository get() {
        return Preconditions.checkNotNullFromComponent(templatesFeatureDependencies.getTimeTaskRepository());
      }
    }

    private static final class GetSchedulesRepositoryProvider implements Provider<ScheduleRepository> {
      private final TemplatesFeatureDependencies templatesFeatureDependencies;

      GetSchedulesRepositoryProvider(TemplatesFeatureDependencies templatesFeatureDependencies) {
        this.templatesFeatureDependencies = templatesFeatureDependencies;
      }

      @Override
      public ScheduleRepository get() {
        return Preconditions.checkNotNullFromComponent(templatesFeatureDependencies.getSchedulesRepository());
      }
    }

    private static final class GetTimeOverlayManagerProvider implements Provider<TimeOverlayManager> {
      private final TemplatesFeatureDependencies templatesFeatureDependencies;

      GetTimeOverlayManagerProvider(TemplatesFeatureDependencies templatesFeatureDependencies) {
        this.templatesFeatureDependencies = templatesFeatureDependencies;
      }

      @Override
      public TimeOverlayManager get() {
        return Preconditions.checkNotNullFromComponent(templatesFeatureDependencies.getTimeOverlayManager());
      }
    }

    private static final class GetDateMangerProvider implements Provider<DateManager> {
      private final TemplatesFeatureDependencies templatesFeatureDependencies;

      GetDateMangerProvider(TemplatesFeatureDependencies templatesFeatureDependencies) {
        this.templatesFeatureDependencies = templatesFeatureDependencies;
      }

      @Override
      public DateManager get() {
        return Preconditions.checkNotNullFromComponent(templatesFeatureDependencies.getDateManger());
      }
    }

    private static final class GetMainCategoryRepositoryProvider implements Provider<MainCategoryRepository> {
      private final TemplatesFeatureDependencies templatesFeatureDependencies;

      GetMainCategoryRepositoryProvider(TemplatesFeatureDependencies templatesFeatureDependencies) {
        this.templatesFeatureDependencies = templatesFeatureDependencies;
      }

      @Override
      public MainCategoryRepository get() {
        return Preconditions.checkNotNullFromComponent(templatesFeatureDependencies.getMainCategoryRepository());
      }
    }

    private static final class GetTimeTaskAlarmManagerProvider implements Provider<TimeTaskAlarmManager> {
      private final TemplatesFeatureDependencies templatesFeatureDependencies;

      GetTimeTaskAlarmManagerProvider(TemplatesFeatureDependencies templatesFeatureDependencies) {
        this.templatesFeatureDependencies = templatesFeatureDependencies;
      }

      @Override
      public TimeTaskAlarmManager get() {
        return Preconditions.checkNotNullFromComponent(templatesFeatureDependencies.getTimeTaskAlarmManager());
      }
    }

    private static final class GetTemplatesAlarmManagerProvider implements Provider<TemplatesAlarmManager> {
      private final TemplatesFeatureDependencies templatesFeatureDependencies;

      GetTemplatesAlarmManagerProvider(TemplatesFeatureDependencies templatesFeatureDependencies) {
        this.templatesFeatureDependencies = templatesFeatureDependencies;
      }

      @Override
      public TemplatesAlarmManager get() {
        return Preconditions.checkNotNullFromComponent(templatesFeatureDependencies.getTemplatesAlarmManager());
      }
    }

    private static final class GetCoroutineManagerProvider implements Provider<CoroutineManager> {
      private final TemplatesFeatureDependencies templatesFeatureDependencies;

      GetCoroutineManagerProvider(TemplatesFeatureDependencies templatesFeatureDependencies) {
        this.templatesFeatureDependencies = templatesFeatureDependencies;
      }

      @Override
      public CoroutineManager get() {
        return Preconditions.checkNotNullFromComponent(templatesFeatureDependencies.getCoroutineManager());
      }
    }
  }
}

package ru.aleshin.features.overview.impl.di.component;

import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import javax.annotation.processing.Generated;
import ru.aleshin.core.domain.common.ScheduleStatusChecker;
import ru.aleshin.core.domain.common.TimeOverlayManager;
import ru.aleshin.core.domain.common.TimeTaskStatusChecker;
import ru.aleshin.core.domain.repository.MainCategoryRepository;
import ru.aleshin.core.domain.repository.ScheduleRepository;
import ru.aleshin.core.domain.repository.TemplatesRepository;
import ru.aleshin.core.domain.repository.TimeTaskRepository;
import ru.aleshin.core.domain.repository.UndefinedTaskRepository;
import ru.aleshin.core.utils.managers.CoroutineManager;
import ru.aleshin.core.utils.managers.DateManager;
import ru.aleshin.features.overview.api.OverviewContentProviderFactory;
import ru.aleshin.features.overview.impl.di.OverviewFeatureDependencies;
import ru.aleshin.features.overview.impl.domain.common.OverviewEitherWrapper;
import ru.aleshin.features.overview.impl.domain.common.OverviewEitherWrapper_Base_Factory;
import ru.aleshin.features.overview.impl.domain.common.OverviewErrorHandler_Base_Factory;
import ru.aleshin.features.overview.impl.domain.interactors.MainCategoriesInteractor;
import ru.aleshin.features.overview.impl.domain.interactors.MainCategoriesInteractor_Base_Factory;
import ru.aleshin.features.overview.impl.domain.interactors.ScheduleInteractor;
import ru.aleshin.features.overview.impl.domain.interactors.ScheduleInteractor_Base_Factory;
import ru.aleshin.features.overview.impl.domain.interactors.ShareTextInteractor;
import ru.aleshin.features.overview.impl.domain.interactors.ShareTextInteractor_Base_Factory;
import ru.aleshin.features.overview.impl.domain.interactors.UndefinedTasksInteractor;
import ru.aleshin.features.overview.impl.domain.interactors.UndefinedTasksInteractor_Base_Factory;
import ru.aleshin.features.overview.impl.navigation.DefaultOverviewContentProviderFactory;
import ru.aleshin.features.overview.impl.navigation.DefaultOverviewContentProviderFactory_Factory;
import ru.aleshin.features.overview.impl.presentation.ui.details.store.DetailsComposeStore;
import ru.aleshin.features.overview.impl.presentation.ui.details.store.DetailsComposeStore_Factory_Factory;
import ru.aleshin.features.overview.impl.presentation.ui.details.store.DetailsWorkProcessor;
import ru.aleshin.features.overview.impl.presentation.ui.details.store.DetailsWorkProcessor_Base_Factory;
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewComposeStore;
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewComposeStore_Factory_Factory;
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewWorkProcessor;
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewWorkProcessor_Base_Factory;

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
public final class DaggerOverviewComponent {
  private DaggerOverviewComponent() {
  }

  public static OverviewComponent.Builder builder() {
    return new Builder();
  }

  private static final class Builder implements OverviewComponent.Builder {
    private OverviewFeatureDependencies overviewFeatureDependencies;

    @Override
    public Builder dependencies(OverviewFeatureDependencies deps) {
      this.overviewFeatureDependencies = Preconditions.checkNotNull(deps);
      return this;
    }

    @Override
    public OverviewComponent build() {
      Preconditions.checkBuilderRequirement(overviewFeatureDependencies, OverviewFeatureDependencies.class);
      return new OverviewComponentImpl(overviewFeatureDependencies);
    }
  }

  private static final class OverviewComponentImpl implements OverviewComponent {
    private final OverviewComponentImpl overviewComponentImpl = this;

    Provider<ScheduleRepository> getSchedulesRepositoryProvider;

    Provider<TimeTaskRepository> getTimeTaskRepositoryProvider;

    Provider<TemplatesRepository> getTemplatesRepositoryProvider;

    Provider<ScheduleStatusChecker> getScheduleStatusCheckerProvider;

    Provider<TimeTaskStatusChecker> getTaskStatusManagerProvider;

    Provider<DateManager> getDateMangerProvider;

    Provider<TimeOverlayManager> getTimeOverlayManagerProvider;

    Provider<OverviewEitherWrapper.Base> baseProvider;

    Provider<ScheduleInteractor.Base> baseProvider2;

    Provider<MainCategoryRepository> getMainCategoryRepositoryProvider;

    Provider<MainCategoriesInteractor.Base> baseProvider3;

    Provider<UndefinedTaskRepository> getUndefinedTaskRepositoryProvider;

    Provider<UndefinedTasksInteractor.Base> baseProvider4;

    Provider<ShareTextInteractor.Base> baseProvider5;

    Provider<OverviewWorkProcessor.Base> baseProvider6;

    Provider<OverviewWorkProcessor> bindOverviewProcessorProvider;

    Provider<CoroutineManager> getCoroutineManagerProvider;

    Provider<OverviewComposeStore.Factory> factoryProvider;

    Provider<DetailsWorkProcessor.Base> baseProvider7;

    Provider<DetailsWorkProcessor> bindDetailsProcessorProvider;

    Provider<DetailsComposeStore.Factory> factoryProvider2;

    Provider<DefaultOverviewContentProviderFactory> defaultOverviewContentProviderFactoryProvider;

    Provider<OverviewContentProviderFactory> bindContentProviderFactoryProvider;

    OverviewComponentImpl(OverviewFeatureDependencies overviewFeatureDependenciesParam) {

      initialize(overviewFeatureDependenciesParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final OverviewFeatureDependencies overviewFeatureDependenciesParam) {
      this.getSchedulesRepositoryProvider = new GetSchedulesRepositoryProvider(overviewFeatureDependenciesParam);
      this.getTimeTaskRepositoryProvider = new GetTimeTaskRepositoryProvider(overviewFeatureDependenciesParam);
      this.getTemplatesRepositoryProvider = new GetTemplatesRepositoryProvider(overviewFeatureDependenciesParam);
      this.getScheduleStatusCheckerProvider = new GetScheduleStatusCheckerProvider(overviewFeatureDependenciesParam);
      this.getTaskStatusManagerProvider = new GetTaskStatusManagerProvider(overviewFeatureDependenciesParam);
      this.getDateMangerProvider = new GetDateMangerProvider(overviewFeatureDependenciesParam);
      this.getTimeOverlayManagerProvider = new GetTimeOverlayManagerProvider(overviewFeatureDependenciesParam);
      this.baseProvider = OverviewEitherWrapper_Base_Factory.create(((Provider) (OverviewErrorHandler_Base_Factory.create())));
      this.baseProvider2 = ScheduleInteractor_Base_Factory.create(getSchedulesRepositoryProvider, getTimeTaskRepositoryProvider, getTemplatesRepositoryProvider, getScheduleStatusCheckerProvider, getTaskStatusManagerProvider, getDateMangerProvider, getTimeOverlayManagerProvider, ((Provider) (baseProvider)));
      this.getMainCategoryRepositoryProvider = new GetMainCategoryRepositoryProvider(overviewFeatureDependenciesParam);
      this.baseProvider3 = MainCategoriesInteractor_Base_Factory.create(getMainCategoryRepositoryProvider, ((Provider) (baseProvider)));
      this.getUndefinedTaskRepositoryProvider = new GetUndefinedTaskRepositoryProvider(overviewFeatureDependenciesParam);
      this.baseProvider4 = UndefinedTasksInteractor_Base_Factory.create(getUndefinedTaskRepositoryProvider, ((Provider) (baseProvider)));
      this.baseProvider5 = ShareTextInteractor_Base_Factory.create(getMainCategoryRepositoryProvider, getDateMangerProvider, ((Provider) (baseProvider)));
      this.baseProvider6 = OverviewWorkProcessor_Base_Factory.create(((Provider) (baseProvider2)), ((Provider) (baseProvider3)), ((Provider) (baseProvider4)), ((Provider) (baseProvider5)), getDateMangerProvider);
      this.bindOverviewProcessorProvider = DoubleCheck.provider((Provider) (baseProvider6));
      this.getCoroutineManagerProvider = new GetCoroutineManagerProvider(overviewFeatureDependenciesParam);
      this.factoryProvider = OverviewComposeStore_Factory_Factory.create(bindOverviewProcessorProvider, getCoroutineManagerProvider);
      this.baseProvider7 = DetailsWorkProcessor_Base_Factory.create(((Provider) (baseProvider2)), getDateMangerProvider);
      this.bindDetailsProcessorProvider = DoubleCheck.provider((Provider) (baseProvider7));
      this.factoryProvider2 = DetailsComposeStore_Factory_Factory.create(bindDetailsProcessorProvider, getCoroutineManagerProvider);
      this.defaultOverviewContentProviderFactoryProvider = DefaultOverviewContentProviderFactory_Factory.create(factoryProvider, factoryProvider2);
      this.bindContentProviderFactoryProvider = DoubleCheck.provider((Provider) (defaultOverviewContentProviderFactoryProvider));
    }

    @Override
    public OverviewContentProviderFactory contentProviderFactory() {
      return bindContentProviderFactoryProvider.get();
    }

    private static final class GetSchedulesRepositoryProvider implements Provider<ScheduleRepository> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetSchedulesRepositoryProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public ScheduleRepository get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getSchedulesRepository());
      }
    }

    private static final class GetTimeTaskRepositoryProvider implements Provider<TimeTaskRepository> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetTimeTaskRepositoryProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public TimeTaskRepository get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getTimeTaskRepository());
      }
    }

    private static final class GetTemplatesRepositoryProvider implements Provider<TemplatesRepository> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetTemplatesRepositoryProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public TemplatesRepository get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getTemplatesRepository());
      }
    }

    private static final class GetScheduleStatusCheckerProvider implements Provider<ScheduleStatusChecker> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetScheduleStatusCheckerProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public ScheduleStatusChecker get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getScheduleStatusChecker());
      }
    }

    private static final class GetTaskStatusManagerProvider implements Provider<TimeTaskStatusChecker> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetTaskStatusManagerProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public TimeTaskStatusChecker get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getTaskStatusManager());
      }
    }

    private static final class GetDateMangerProvider implements Provider<DateManager> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetDateMangerProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public DateManager get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getDateManger());
      }
    }

    private static final class GetTimeOverlayManagerProvider implements Provider<TimeOverlayManager> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetTimeOverlayManagerProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public TimeOverlayManager get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getTimeOverlayManager());
      }
    }

    private static final class GetMainCategoryRepositoryProvider implements Provider<MainCategoryRepository> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetMainCategoryRepositoryProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public MainCategoryRepository get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getMainCategoryRepository());
      }
    }

    private static final class GetUndefinedTaskRepositoryProvider implements Provider<UndefinedTaskRepository> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetUndefinedTaskRepositoryProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public UndefinedTaskRepository get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getUndefinedTaskRepository());
      }
    }

    private static final class GetCoroutineManagerProvider implements Provider<CoroutineManager> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetCoroutineManagerProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public CoroutineManager get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getCoroutineManager());
      }
    }
  }
}

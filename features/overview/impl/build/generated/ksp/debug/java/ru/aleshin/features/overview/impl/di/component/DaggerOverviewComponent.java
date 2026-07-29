package ru.aleshin.features.overview.impl.di.component;

import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import javax.annotation.processing.Generated;
import ru.aleshin.core.domain.common.GoalProgressManager;
import ru.aleshin.core.domain.common.RecurringScheduleManager;
import ru.aleshin.core.domain.common.ScheduleStatusChecker;
import ru.aleshin.core.domain.common.TimeTaskStatusChecker;
import ru.aleshin.core.domain.repository.GoalHistoryRepository;
import ru.aleshin.core.domain.repository.GoalRepository;
import ru.aleshin.core.domain.repository.MainCategoryRepository;
import ru.aleshin.core.domain.repository.ScheduleRepository;
import ru.aleshin.core.domain.repository.TimeTaskRepository;
import ru.aleshin.core.domain.repository.UndefinedTaskRepository;
import ru.aleshin.core.utils.managers.CoroutineManager;
import ru.aleshin.core.utils.managers.DateManager;
import ru.aleshin.features.overview.api.OverviewContentProviderFactory;
import ru.aleshin.features.overview.impl.di.OverviewFeatureDependencies;
import ru.aleshin.features.overview.impl.domain.common.OverviewEitherWrapper;
import ru.aleshin.features.overview.impl.domain.common.OverviewEitherWrapper_Base_Factory;
import ru.aleshin.features.overview.impl.domain.common.OverviewErrorHandler_Base_Factory;
import ru.aleshin.features.overview.impl.domain.interactors.GoalsHistoryInteractor;
import ru.aleshin.features.overview.impl.domain.interactors.GoalsHistoryInteractor_Base_Factory;
import ru.aleshin.features.overview.impl.domain.interactors.GoalsInteractor;
import ru.aleshin.features.overview.impl.domain.interactors.GoalsInteractor_Base_Factory;
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
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.store.GoalDetailsComposeStore;
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.store.GoalDetailsComposeStore_Factory_Factory;
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.store.GoalDetailsWorkProcessor;
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.store.GoalDetailsWorkProcessor_Base_Factory;
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.store.GoalsHistoryComposeStore;
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.store.GoalsHistoryComposeStore_Factory_Factory;
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.store.GoalsHistoryWorkProcessor;
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.store.GoalsHistoryWorkProcessor_Base_Factory;
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

    Provider<RecurringScheduleManager> getRecurringScheduleManagerProvider;

    Provider<ScheduleStatusChecker> getScheduleStatusCheckerProvider;

    Provider<TimeTaskStatusChecker> getTaskStatusManagerProvider;

    Provider<DateManager> getDateMangerProvider;

    Provider<OverviewEitherWrapper.Base> baseProvider;

    Provider<ScheduleInteractor.Base> baseProvider2;

    Provider<GoalRepository> getGoalRepositoryProvider;

    Provider<GoalProgressManager> getGoalProgressManagerProvider;

    Provider<GoalsInteractor.Base> baseProvider3;

    Provider<GoalHistoryRepository> getGoalHistoryRepositoryProvider;

    Provider<GoalsHistoryInteractor.Base> baseProvider4;

    Provider<MainCategoryRepository> getMainCategoryRepositoryProvider;

    Provider<MainCategoriesInteractor.Base> baseProvider5;

    Provider<UndefinedTaskRepository> getUndefinedTaskRepositoryProvider;

    Provider<UndefinedTasksInteractor.Base> baseProvider6;

    Provider<ShareTextInteractor.Base> baseProvider7;

    Provider<OverviewWorkProcessor.Base> baseProvider8;

    Provider<OverviewWorkProcessor> bindOverviewProcessorProvider;

    Provider<CoroutineManager> getCoroutineManagerProvider;

    Provider<OverviewComposeStore.Factory> factoryProvider;

    Provider<GoalDetailsWorkProcessor.Base> baseProvider9;

    Provider<GoalDetailsWorkProcessor> bindGoalDetailsProcessorProvider;

    Provider<GoalDetailsComposeStore.Factory> factoryProvider2;

    Provider<GoalsHistoryWorkProcessor.Base> baseProvider10;

    Provider<GoalsHistoryWorkProcessor> bindGoalsHistoryProcessorProvider;

    Provider<GoalsHistoryComposeStore.Factory> factoryProvider3;

    Provider<DefaultOverviewContentProviderFactory> defaultOverviewContentProviderFactoryProvider;

    Provider<OverviewContentProviderFactory> bindContentProviderFactoryProvider;

    OverviewComponentImpl(OverviewFeatureDependencies overviewFeatureDependenciesParam) {

      initialize(overviewFeatureDependenciesParam);
      initialize2(overviewFeatureDependenciesParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final OverviewFeatureDependencies overviewFeatureDependenciesParam) {
      this.getSchedulesRepositoryProvider = new GetSchedulesRepositoryProvider(overviewFeatureDependenciesParam);
      this.getTimeTaskRepositoryProvider = new GetTimeTaskRepositoryProvider(overviewFeatureDependenciesParam);
      this.getRecurringScheduleManagerProvider = new GetRecurringScheduleManagerProvider(overviewFeatureDependenciesParam);
      this.getScheduleStatusCheckerProvider = new GetScheduleStatusCheckerProvider(overviewFeatureDependenciesParam);
      this.getTaskStatusManagerProvider = new GetTaskStatusManagerProvider(overviewFeatureDependenciesParam);
      this.getDateMangerProvider = new GetDateMangerProvider(overviewFeatureDependenciesParam);
      this.baseProvider = OverviewEitherWrapper_Base_Factory.create(((Provider) (OverviewErrorHandler_Base_Factory.create())));
      this.baseProvider2 = ScheduleInteractor_Base_Factory.create(getSchedulesRepositoryProvider, getTimeTaskRepositoryProvider, getRecurringScheduleManagerProvider, getScheduleStatusCheckerProvider, getTaskStatusManagerProvider, getDateMangerProvider, ((Provider) (baseProvider)));
      this.getGoalRepositoryProvider = new GetGoalRepositoryProvider(overviewFeatureDependenciesParam);
      this.getGoalProgressManagerProvider = new GetGoalProgressManagerProvider(overviewFeatureDependenciesParam);
      this.baseProvider3 = GoalsInteractor_Base_Factory.create(getGoalRepositoryProvider, getTimeTaskRepositoryProvider, getGoalProgressManagerProvider, getDateMangerProvider, ((Provider) (baseProvider)));
      this.getGoalHistoryRepositoryProvider = new GetGoalHistoryRepositoryProvider(overviewFeatureDependenciesParam);
      this.baseProvider4 = GoalsHistoryInteractor_Base_Factory.create(getGoalRepositoryProvider, getGoalHistoryRepositoryProvider, getTimeTaskRepositoryProvider, getGoalProgressManagerProvider, getDateMangerProvider, ((Provider) (baseProvider)));
      this.getMainCategoryRepositoryProvider = new GetMainCategoryRepositoryProvider(overviewFeatureDependenciesParam);
      this.baseProvider5 = MainCategoriesInteractor_Base_Factory.create(getMainCategoryRepositoryProvider, ((Provider) (baseProvider)));
      this.getUndefinedTaskRepositoryProvider = new GetUndefinedTaskRepositoryProvider(overviewFeatureDependenciesParam);
      this.baseProvider6 = UndefinedTasksInteractor_Base_Factory.create(getUndefinedTaskRepositoryProvider, ((Provider) (baseProvider)));
      this.baseProvider7 = ShareTextInteractor_Base_Factory.create(getMainCategoryRepositoryProvider, getDateMangerProvider, ((Provider) (baseProvider)));
      this.baseProvider8 = OverviewWorkProcessor_Base_Factory.create(((Provider) (baseProvider2)), ((Provider) (baseProvider3)), ((Provider) (baseProvider4)), ((Provider) (baseProvider5)), ((Provider) (baseProvider6)), ((Provider) (baseProvider7)), getDateMangerProvider);
      this.bindOverviewProcessorProvider = DoubleCheck.provider((Provider) (baseProvider8));
      this.getCoroutineManagerProvider = new GetCoroutineManagerProvider(overviewFeatureDependenciesParam);
      this.factoryProvider = OverviewComposeStore_Factory_Factory.create(bindOverviewProcessorProvider, getCoroutineManagerProvider);
      this.baseProvider9 = GoalDetailsWorkProcessor_Base_Factory.create(((Provider) (baseProvider3)));
      this.bindGoalDetailsProcessorProvider = DoubleCheck.provider((Provider) (baseProvider9));
      this.factoryProvider2 = GoalDetailsComposeStore_Factory_Factory.create(bindGoalDetailsProcessorProvider, getCoroutineManagerProvider);
    }

    @SuppressWarnings("unchecked")
    private void initialize2(final OverviewFeatureDependencies overviewFeatureDependenciesParam) {
      this.baseProvider10 = GoalsHistoryWorkProcessor_Base_Factory.create(((Provider) (baseProvider4)));
      this.bindGoalsHistoryProcessorProvider = DoubleCheck.provider((Provider) (baseProvider10));
      this.factoryProvider3 = GoalsHistoryComposeStore_Factory_Factory.create(bindGoalsHistoryProcessorProvider, getCoroutineManagerProvider);
      this.defaultOverviewContentProviderFactoryProvider = DefaultOverviewContentProviderFactory_Factory.create(factoryProvider, factoryProvider2, factoryProvider3);
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

    private static final class GetRecurringScheduleManagerProvider implements Provider<RecurringScheduleManager> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetRecurringScheduleManagerProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public RecurringScheduleManager get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getRecurringScheduleManager());
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

    private static final class GetGoalRepositoryProvider implements Provider<GoalRepository> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetGoalRepositoryProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public GoalRepository get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getGoalRepository());
      }
    }

    private static final class GetGoalProgressManagerProvider implements Provider<GoalProgressManager> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetGoalProgressManagerProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public GoalProgressManager get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getGoalProgressManager());
      }
    }

    private static final class GetGoalHistoryRepositoryProvider implements Provider<GoalHistoryRepository> {
      private final OverviewFeatureDependencies overviewFeatureDependencies;

      GetGoalHistoryRepositoryProvider(OverviewFeatureDependencies overviewFeatureDependencies) {
        this.overviewFeatureDependencies = overviewFeatureDependencies;
      }

      @Override
      public GoalHistoryRepository get() {
        return Preconditions.checkNotNullFromComponent(overviewFeatureDependencies.getGoalHistoryRepository());
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

package ru.aleshin.features.overview.impl.domain.interactors;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.domain.repository.UndefinedTaskRepository;
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
public final class UndefinedTasksInteractor_Base_Factory implements Factory<UndefinedTasksInteractor.Base> {
  private final Provider<UndefinedTaskRepository> undefinedTaskRepositoryProvider;

  private final Provider<OverviewEitherWrapper> eitherWrapperProvider;

  private UndefinedTasksInteractor_Base_Factory(
      Provider<UndefinedTaskRepository> undefinedTaskRepositoryProvider,
      Provider<OverviewEitherWrapper> eitherWrapperProvider) {
    this.undefinedTaskRepositoryProvider = undefinedTaskRepositoryProvider;
    this.eitherWrapperProvider = eitherWrapperProvider;
  }

  @Override
  public UndefinedTasksInteractor.Base get() {
    return newInstance(undefinedTaskRepositoryProvider.get(), eitherWrapperProvider.get());
  }

  public static UndefinedTasksInteractor_Base_Factory create(
      Provider<UndefinedTaskRepository> undefinedTaskRepositoryProvider,
      Provider<OverviewEitherWrapper> eitherWrapperProvider) {
    return new UndefinedTasksInteractor_Base_Factory(undefinedTaskRepositoryProvider, eitherWrapperProvider);
  }

  public static UndefinedTasksInteractor.Base newInstance(
      UndefinedTaskRepository undefinedTaskRepository, OverviewEitherWrapper eitherWrapper) {
    return new UndefinedTasksInteractor.Base(undefinedTaskRepository, eitherWrapper);
  }
}

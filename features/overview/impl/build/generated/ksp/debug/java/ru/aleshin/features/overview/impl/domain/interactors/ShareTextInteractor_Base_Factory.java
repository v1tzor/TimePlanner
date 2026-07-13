package ru.aleshin.features.overview.impl.domain.interactors;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.domain.repository.MainCategoryRepository;
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
public final class ShareTextInteractor_Base_Factory implements Factory<ShareTextInteractor.Base> {
  private final Provider<MainCategoryRepository> categoryRepositoryProvider;

  private final Provider<DateManager> dateManagerProvider;

  private final Provider<OverviewEitherWrapper> eitherWrapperProvider;

  private ShareTextInteractor_Base_Factory(
      Provider<MainCategoryRepository> categoryRepositoryProvider,
      Provider<DateManager> dateManagerProvider,
      Provider<OverviewEitherWrapper> eitherWrapperProvider) {
    this.categoryRepositoryProvider = categoryRepositoryProvider;
    this.dateManagerProvider = dateManagerProvider;
    this.eitherWrapperProvider = eitherWrapperProvider;
  }

  @Override
  public ShareTextInteractor.Base get() {
    return newInstance(categoryRepositoryProvider.get(), dateManagerProvider.get(), eitherWrapperProvider.get());
  }

  public static ShareTextInteractor_Base_Factory create(
      Provider<MainCategoryRepository> categoryRepositoryProvider,
      Provider<DateManager> dateManagerProvider,
      Provider<OverviewEitherWrapper> eitherWrapperProvider) {
    return new ShareTextInteractor_Base_Factory(categoryRepositoryProvider, dateManagerProvider, eitherWrapperProvider);
  }

  public static ShareTextInteractor.Base newInstance(MainCategoryRepository categoryRepository,
      DateManager dateManager, OverviewEitherWrapper eitherWrapper) {
    return new ShareTextInteractor.Base(categoryRepository, dateManager, eitherWrapper);
  }
}

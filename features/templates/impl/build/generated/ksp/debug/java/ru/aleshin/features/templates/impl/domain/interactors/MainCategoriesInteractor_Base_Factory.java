package ru.aleshin.features.templates.impl.domain.interactors;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.domain.repository.MainCategoryRepository;
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
public final class MainCategoriesInteractor_Base_Factory implements Factory<MainCategoriesInteractor.Base> {
  private final Provider<MainCategoryRepository> mainCategoryRepositoryProvider;

  private final Provider<HomeEitherWrapper> eitherWrapperProvider;

  private MainCategoriesInteractor_Base_Factory(
      Provider<MainCategoryRepository> mainCategoryRepositoryProvider,
      Provider<HomeEitherWrapper> eitherWrapperProvider) {
    this.mainCategoryRepositoryProvider = mainCategoryRepositoryProvider;
    this.eitherWrapperProvider = eitherWrapperProvider;
  }

  @Override
  public MainCategoriesInteractor.Base get() {
    return newInstance(mainCategoryRepositoryProvider.get(), eitherWrapperProvider.get());
  }

  public static MainCategoriesInteractor_Base_Factory create(
      Provider<MainCategoryRepository> mainCategoryRepositoryProvider,
      Provider<HomeEitherWrapper> eitherWrapperProvider) {
    return new MainCategoriesInteractor_Base_Factory(mainCategoryRepositoryProvider, eitherWrapperProvider);
  }

  public static MainCategoriesInteractor.Base newInstance(
      MainCategoryRepository mainCategoryRepository, HomeEitherWrapper eitherWrapper) {
    return new MainCategoriesInteractor.Base(mainCategoryRepository, eitherWrapper);
  }
}

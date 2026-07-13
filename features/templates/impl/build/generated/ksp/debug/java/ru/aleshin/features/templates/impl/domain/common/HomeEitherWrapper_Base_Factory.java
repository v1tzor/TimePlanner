package ru.aleshin.features.templates.impl.domain.common;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class HomeEitherWrapper_Base_Factory implements Factory<HomeEitherWrapper.Base> {
  private final Provider<HomeErrorHandler> errorHandlerProvider;

  private HomeEitherWrapper_Base_Factory(Provider<HomeErrorHandler> errorHandlerProvider) {
    this.errorHandlerProvider = errorHandlerProvider;
  }

  @Override
  public HomeEitherWrapper.Base get() {
    return newInstance(errorHandlerProvider.get());
  }

  public static HomeEitherWrapper_Base_Factory create(
      Provider<HomeErrorHandler> errorHandlerProvider) {
    return new HomeEitherWrapper_Base_Factory(errorHandlerProvider);
  }

  public static HomeEitherWrapper.Base newInstance(HomeErrorHandler errorHandler) {
    return new HomeEitherWrapper.Base(errorHandler);
  }
}

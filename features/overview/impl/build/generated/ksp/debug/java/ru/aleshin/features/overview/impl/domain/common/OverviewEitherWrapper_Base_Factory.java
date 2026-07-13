package ru.aleshin.features.overview.impl.domain.common;

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
public final class OverviewEitherWrapper_Base_Factory implements Factory<OverviewEitherWrapper.Base> {
  private final Provider<OverviewErrorHandler> errorHandlerProvider;

  private OverviewEitherWrapper_Base_Factory(Provider<OverviewErrorHandler> errorHandlerProvider) {
    this.errorHandlerProvider = errorHandlerProvider;
  }

  @Override
  public OverviewEitherWrapper.Base get() {
    return newInstance(errorHandlerProvider.get());
  }

  public static OverviewEitherWrapper_Base_Factory create(
      Provider<OverviewErrorHandler> errorHandlerProvider) {
    return new OverviewEitherWrapper_Base_Factory(errorHandlerProvider);
  }

  public static OverviewEitherWrapper.Base newInstance(OverviewErrorHandler errorHandler) {
    return new OverviewEitherWrapper.Base(errorHandler);
  }
}

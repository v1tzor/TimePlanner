pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "TimePlanner"

include(":app")
include(":core:utils")
include(":core:ui")
include(":module-injector")
include(":features:home:api")
include(":features:home:impl")
include(":features:editor:api")
include(":features:editor:impl")
include(":features:analytics:api")
include(":features:analytics:impl")
include(":features:settings:api")
include(":features:settings:impl")

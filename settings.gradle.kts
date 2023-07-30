/*
 * Copyright 2023 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

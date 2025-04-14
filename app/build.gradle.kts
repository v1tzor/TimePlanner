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

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import ru.ok.tracer.mapping_plugin.TracerConfig

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    alias(libs.plugins.parcelize)
}

val localProperties = gradleLocalProperties(rootDir, providers)

val hasRustore = gradle.startParameter.taskNames.any {
    it.contains("RustoreDebug", ignoreCase = true) || it.contains("RustoreRelease", ignoreCase = true)
}

val hasHuawei = gradle.startParameter.taskNames.any {
    it.contains("HuaweiDebug", ignoreCase = true) || it.contains("HuaweiRelease", ignoreCase = true)
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.tracer.plugin)
    }
}

android {
    namespace = libs.versions.applicationId.get()
    compileSdk = libs.versions.compileSdkVersion.get().toIntOrNull()
    flavorDimensions += libs.versions.productionDimension.get()

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.minSdkVersion.get().toIntOrNull()
        targetSdk = libs.versions.targetSdkVersion.get().toIntOrNull()
        compileSdk = libs.versions.compileSdkVersion.get().toIntOrNull()

        versionCode = libs.versions.versionCode.get().toIntOrNull()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = libs.versions.testInstrumentRunner.get()
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties.getProperty("storeFile"))
            storePassword = localProperties.getProperty("storePassword")
            keyAlias = localProperties.getProperty("keyAlias")
            keyPassword = localProperties.getProperty("keyPassword")
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
        getByName("debug") {
            storeFile = file(localProperties.getProperty("storeFile"))
            storePassword = localProperties.getProperty("storePassword")
            keyAlias = localProperties.getProperty("keyAlias")
            keyPassword = localProperties.getProperty("keyPassword")
        }
    }

    buildTypes {
        getByName("release") {
            isCrunchPngs = false
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    productFlavors {
        create("fdroid") {
            dimension = libs.versions.productionDimension.get()
        }
        create("rustore") {
            dimension = libs.versions.productionDimension.get()
            val myTrackerKey = localProperties.getProperty("myTrackerKey")
            buildConfigField("String", "MY_TRACKER_KEY", "\"$myTrackerKey\"")
        }
        create("huawei") {
            dimension = libs.versions.productionDimension.get()
            val myTrackerKey = localProperties.getProperty("myTrackerKey")
            buildConfigField("String", "MY_TRACKER_KEY", "\"$myTrackerKey\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompiler.get()
    }

    packaging {
        resources {
            resources.pickFirsts.add("META-INF/INDEX.LIST")
            resources.merges.add("META-INF/DEPENDENCIES")
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

val rustoreImplementation = "rustoreImplementation"
val fdroidImplementation = "fdroidImplementation"
val huaweiImplementation = "huaweiImplementation"

dependencies {

    implementation(project(":core:utils"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    implementation(project(":features:home:api"))
    implementation(project(":features:home:impl"))
    implementation(project(":features:editor:api"))
    implementation(project(":features:editor:impl"))
    implementation(project(":features:analytics:api"))
    implementation(project(":features:analytics:impl"))
    implementation(project(":features:settings:api"))
    implementation(project(":features:settings:impl"))

    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.compose)

    ksp(libs.dagger.ksp)

    implementation(libs.room.core)
    ksp(libs.room.ksp)

    testImplementation(libs.jUnit)
    androidTestImplementation(libs.jUnitExt)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.composeJUnit)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.testmanifest)

    debugImplementation(libs.leakcanary)

    rustoreImplementation(platform(libs.tracer.bom))
    rustoreImplementation(libs.bundles.tracer)
    rustoreImplementation(libs.mytracker.core)

    huaweiImplementation(platform(libs.tracer.bom))
    huaweiImplementation(libs.bundles.tracer)
    huaweiImplementation(libs.mytracker.core)
}

if (hasRustore || hasHuawei) {
    plugins.apply(libs.plugins.tracer.get().pluginId)
    project.extensions.configure<NamedDomainObjectContainer<TracerConfig>> {
        create("defaultConfig") {
            pluginToken = localProperties.getProperty("tracerPluginToken")
            appToken = localProperties.getProperty("tracerAppToken")
            uploadMapping = true
            uploadNativeSymbols = true
        }
    }
}
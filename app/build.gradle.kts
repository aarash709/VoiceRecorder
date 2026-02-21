import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlinx.ksp)
}

android {
    compileSdk = 36
    namespace = "com.experiment.voicerecorder"
    defaultConfig {
        applicationId = "com.experiment.voicerecorder"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1-alpha"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin.compilerOptions.jvmTarget= JvmTarget.JVM_17
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(project(":feature:record"))
    implementation(project(":feature:playlist"))
    implementation(project(":feature:settings"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:service"))

    detektPlugins(libs.detekt.formatting)

    implementation(libs.androidx.coreKtx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.splashScreen)

//    //necessary for notification style using agp:7.1.2
    implementation(libs.androidx.media)

    implementation(libs.kotlix.coroutines.core)
    implementation(libs.kotlix.coroutines.android)

    implementation(libs.material)
    implementation(libs.androidx.lifecycleRuntimeKtx)
    implementation(libs.androidx.lifecycleComposeRuntime)
    implementation(libs.androidx.activity)

    implementation(libs.androidx.navigationCompose)
    implementation(libs.androidx.lifecycleViewModelCompose)
    implementation(libs.androidx.activityCompose)
    implementation(libs.androidx.navigationCompose)

    implementation(libs.accompanist.permissions)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit4)
    testImplementation(libs.kotlix.coroutines.test)
    androidTestImplementation(project(":core:designsystem")) // using ui tests apis (add test apis with a test module)


}

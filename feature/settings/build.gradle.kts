import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlinx.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.recorder.feature.settings"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
    }

    buildFeatures{
        compose =  true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin.compilerOptions.jvmTarget= JvmTarget.JVM_17
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:datastore"))

    implementation(libs.androidx.navigationCompose)

    implementation(libs.androidx.lifecycleComposeRuntime)

    implementation(libs.androidx.coreKtx)
    implementation(libs.androidx.appcompat)

    implementation(libs.hilt.navigationCompose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)


    //test
    testImplementation(libs.junit4)
    testImplementation(libs.kotlix.coroutines.test)
    androidTestImplementation(libs.kotlix.coroutines.test)
    androidTestImplementation(libs.androidx.test.extJunit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.testJunit)
}
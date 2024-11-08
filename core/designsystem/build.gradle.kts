plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
}

android {
    namespace = "com.example.designsystem"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    api(platform(libs.compose.bom))
    api(libs.bundles.compose)
    debugApi(libs.compose.ui.tooling)

    testImplementation(libs.junit4)
    testImplementation(libs.kotlix.coroutines.test)
    androidTestApi(libs.compose.ui.testJunit)
    androidTestApi(libs.kotlix.coroutines.test)
    androidTestApi(libs.androidx.test.extJunit)
    androidTestApi(libs.espresso.core)
}
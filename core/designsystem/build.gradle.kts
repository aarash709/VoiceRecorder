plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
}

android {
    namespace = "com.example.designsystem"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")
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
    composeOptions{
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }
}

dependencies {
    api(platform(libs.compose.bom))
    api(libs.bundles.compose)
    debugApi(libs.compose.ui.tooling)

    testImplementation(libs.junit4)
    testImplementation(libs.kotlix.coroutinesTest)
    androidTestApi(libs.compose.ui.testJunit)
    androidTestApi(libs.kotlix.coroutinesTest)
    androidTestApi(libs.androidx.test.extJunit)
    androidTestApi(libs.espresso.core)
}
plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.example.designsystem"
    compileSdk = 33

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
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    testImplementation(libs.junit4)
    testImplementation(libs.kotlix.coroutinesTest)
    androidTestImplementation(libs.kotlix.coroutinesTest)
    androidTestImplementation(libs.androidx.test.extJunit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.testJunit)
}
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
}

android {
    namespace = "com.recorder.feature.settings"
    compileSdk = 34

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
    composeOptions{
        kotlinCompilerExtensionVersion =  libs.versions.androidxComposeCompiler.get()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))

    implementation(libs.androidx.navigationCompose)

    implementation(libs.androidx.lifecycleComposeRuntime)

    implementation(libs.androidx.coreKtx)
    implementation(libs.appcompat)

    implementation(libs.hilt.navigationCompose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.kaptCompiler)

    implementation(libs.appcompat)

    //test
    testImplementation(libs.junit4)
    testImplementation(libs.kotlix.coroutinesTest)
    androidTestImplementation(libs.kotlix.coroutinesTest)
    androidTestImplementation(libs.androidx.test.extJunit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.testJunit)
}
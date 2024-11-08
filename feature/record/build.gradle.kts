plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlinx.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.recorder.feature.record"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:service"))
    implementation(project(":core:datastore"))
    implementation(project(":core:designsystem"))


    implementation(libs.androidx.navigationCompose)

    implementation(libs.material)

    implementation(libs.androidx.lifecycleComposeRuntime)

    implementation(libs.kotlix.coroutines.core)
    implementation(libs.kotlix.coroutines.android)

    implementation(libs.hilt.navigationCompose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.coreKtx)
    implementation(libs.androidx.appcompat)

    testImplementation(libs.junit4)
    testImplementation(libs.kotlix.coroutines.test)
    androidTestImplementation(libs.kotlix.coroutines.test)
    androidTestImplementation(libs.androidx.test.extJunit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.testJunit)
}
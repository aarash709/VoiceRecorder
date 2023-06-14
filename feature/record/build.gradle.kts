plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.hilt.get().pluginId)
    id("kotlin-kapt")
}

android {
    namespace = "com.recorder.feature.record"
    compileSdk = 33

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")
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

    implementation(libs.compose.bom)
    implementation(libs.bundles.compose)
    implementation(libs.material)

    implementation(libs.coilCompose)

    implementation(libs.androidx.lifecycleComposeRuntime)

    implementation(libs.kotlix.coroutinesCore)
    implementation(libs.kotlix.coroutinesAndroid)

    implementation(libs.hilt.navigationCompose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.kaptCompiler)

    implementation(libs.androidx.coreKtx)
    implementation(libs.appcompat)

    implementation(libs.timberLogger)

    testImplementation(libs.junit4)
    testImplementation(libs.kotlix.coroutinesTest)
    androidTestImplementation(libs.kotlix.coroutinesTest)
    androidTestImplementation(libs.androidx.test.extJunit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.testJunit)
}
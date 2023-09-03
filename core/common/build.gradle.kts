plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.serialization.get().pluginId)
    id(libs.plugins.hilt.get().pluginId)
    id("kotlin-kapt")
}

android {
    namespace = "com.core.common"
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
}
dependencies {

    implementation(project(":core:designsystem"))

    api(libs.kotlix.serialization)

    implementation(libs.androidx.coreKtx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.hilt.android)
    kapt(libs.hilt.kaptCompiler)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.extJunit)
    androidTestImplementation(libs.espresso.core)

}
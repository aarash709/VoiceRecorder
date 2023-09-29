plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.serialization.get().pluginId)
    id(libs.plugins.hilt.get().pluginId)
    id("kotlin-kapt")
}

android {
    compileSdk = 34
    namespace = "com.experiment.voicerecorder"
    defaultConfig {
        applicationId = "com.experiment.voicerecorder"
        minSdk = 26
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
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
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:service"))


    implementation(libs.androidx.coreKtx)
    implementation(libs.androidx.appCompat)
    implementation(libs.androidx.splashScreen)

//    //necessary for notification style using agp:7.1.2
    implementation(libs.androidx.media)

    implementation(libs.kotlix.coroutinesCore)
    implementation(libs.kotlix.coroutinesAndroid)

    implementation(libs.material)
    implementation(libs.androidx.lifecycleRuntimeKtx)
    implementation(libs.androidx.lifecycleComposeRuntime)
    implementation(libs.androidx.activity)

    implementation(libs.androidx.navigationCompose)
    implementation(libs.androidx.lifecycleViewModelCompose)
    implementation(libs.androidx.activityCompose)
    implementation(libs.androidx.navigationCompose)

//    implementation "com.google.accompanist:accompanist-drawablepainter:$accompanist_version"
    implementation(libs.accompanist.navigationAnimation)
    implementation(libs.accompanist.permissions)

    implementation(libs.coilCompose)

    implementation(libs.hilt.android)
    kapt(libs.hilt.kaptCompiler)

    testImplementation(libs.junit4)
    testImplementation(libs.kotlix.coroutinesTest)
    androidTestImplementation(libs.kotlix.coroutinesTest)
    androidTestImplementation(libs.androidx.test.extJunit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.testJunit)
}
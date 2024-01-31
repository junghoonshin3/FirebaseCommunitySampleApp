plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "kr.sjh.feature.list"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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

    implementation(Deps.core)
    implementation(CoroutinesLifeCycleScope.lifeCycleRuntime)
    implementation(JetPackCompose.composeActivity)
    implementation(JetPackCompose.composeUi)
    implementation(JetPackCompose.uiGraphics)
    implementation(JetPackCompose.composeBom)
    implementation(JetPackCompose.composeUiToolingPreview)
    testImplementation(TestImplementation.junit)
    androidTestImplementation(AndroidTestImplementation.junit)
    androidTestImplementation(AndroidTestImplementation.espressoCore)
    androidTestImplementation(AndroidTestImplementation.composeBom)
    androidTestImplementation(AndroidTestImplementation.junit4)
    debugImplementation(DebugImplementation.uiTooling)
    debugImplementation(DebugImplementation.testManifest)

    //material3
    implementation(JetPackCompose.material3)
    implementation(JetPackCompose.material3WindowSizeClass)

    //navigation
    implementation(Navigation.composeNavigation)

    //Dagger-Hilt
    implementation(DaggerHilt.daggerHilt)
    kapt(DaggerHilt.daggerHiltCompiler)
}
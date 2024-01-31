plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "kr.sjh.pickup"
    compileSdk = 34

    defaultConfig {
        applicationId = "kr.sjh.pickup"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":feature:list"))
    implementation(project(":feature:setting"))
    implementation(project(":feature:chat"))
    implementation(project(":domain:chat"))
    implementation(project(":domain:setting"))
    implementation(project(":domain:list"))
    implementation(project(":data:repository"))
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

    // Splash API
    implementation(SplashScreen.coreSplash)

    //Dagger-Hilt
    implementation(DaggerHilt.daggerHilt)
    kapt(DaggerHilt.daggerHiltCompiler)

}
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id ("com.google.devtools.ksp")
}

android {
    namespace = "kr.sjh.presentation"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
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

    implementation(project(":domain"))
    implementation(Deps.core)
    implementation(CoroutinesLifeCycleScope.lifeCycleRuntime)
    implementation(JetPackCompose.composeActivity)
    implementation(JetPackCompose.composeUi)
    implementation(JetPackCompose.uiGraphics)
    implementation(JetPackCompose.composeBom)
    implementation(JetPackCompose.composeUiToolingPreview)
    implementation(JetPackCompose.composeLifeCycle)
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
    ksp(DaggerHilt.daggerHilt)
    ksp(DaggerHilt.daggerHiltCompiler)
    implementation(DaggerHilt.hiltNavigation)

    //splash api
    implementation(SplashScreen.coreSplash)

    //gilde
    implementation("com.github.skydoves:landscapist-glide:2.3.2")

    //permission
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")


}
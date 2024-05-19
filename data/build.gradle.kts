plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "kr.sjh.data"
    compileSdk = 33

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":model"))
    //Dagger-Hilt
    implementation(DaggerHilt.daggerHilt)
    kapt(DaggerHilt.daggerHiltCompiler)
    implementation(DaggerHilt.hiltNavigation)

    //kakao login
    api(KaKao.kakao_login)

    api(platform("com.google.firebase:firebase-bom:32.3.1"))
    //Firebase DB
    api("com.google.firebase:firebase-database-ktx")
    //Firebase Storage
    api("com.google.firebase:firebase-storage-ktx")


}
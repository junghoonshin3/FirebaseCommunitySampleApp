plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")

}

android {
    namespace = "kr.sjh.domain"
    compileSdk = 34
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    //Dagger-Hilt
    ksp(DaggerHilt.daggerHilt)
    ksp(DaggerHilt.daggerHiltCompiler)
    implementation(DaggerHilt.hiltNavigation)
    api(KaKao.kakao_login)
    api(platform("com.google.firebase:firebase-bom:32.3.1"))
    //Firebase DB
    api("com.google.firebase:firebase-database-ktx")
    //Firebase Storage
    api("com.google.firebase:firebase-storage-ktx")
}
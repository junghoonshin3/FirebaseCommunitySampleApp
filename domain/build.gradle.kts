plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    //Coroutine
    implementation(Coroutines.kotlinxCoroutines)

    //SkyDove Compose Stable Marker
    compileOnly("com.github.skydoves:compose-stable-marker:1.0.4")


}

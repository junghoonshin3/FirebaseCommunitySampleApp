object Versions {
    const val hilt_navigation = "1.1.0-alpha01"
    const val composeViewModel = "2.6.1"
    const val composeNavigation = "2.7.6"
    const val material3WindowSizeClass = "1.1.2"
    const val espressoCore = "3.5.1"
    const val core = "1.9.0"
    const val lifeCycle = "2.6.2"
    const val composeActivity = "1.8.2"
    const val composeUi = "1.6.6"
    const val toolingPreview = "1.5.4"
    const val composeMaterial = "1.1.2"
    const val juint = "4.13.2"
    const val jUnit4 = "1.6.0"
    const val extJunit = "1.1.5"
    const val composeBom = "2023.03.00"
    const val material3 = "1.2.1"
    const val uiTooling = "1.5.4"
    const val testManifest = "1.5.4"
    const val splash = "1.0.1"
    const val hilt_version = "2.48"
    const val kakao_login = "2.19.0"
    const val compose_lifecycle = "2.6.0-alpha01"

}

object CoroutinesLifeCycleScope {
    const val lifeCycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifeCycle}"
}

object JetPackCompose {
    const val composeActivity = "androidx.activity:activity-compose:${Versions.composeActivity}"
    const val composeUi = "androidx.compose.ui:ui:${Versions.composeUi}"
    const val composeUiToolingPreview =
        "androidx.compose.ui:ui-tooling-preview:${Versions.toolingPreview}"
    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
    const val uiGraphics = "androidx.compose.ui:ui-graphics"
    const val material3 = "androidx.compose.material3:material3:${Versions.material3}"
    const val material3WindowSizeClass =
        "androidx.compose.material3:material3-window-size-class:${Versions.material3WindowSizeClass}"
    const val composeLifeCycle =
        "androidx.lifecycle:lifecycle-runtime-compose:${Versions.compose_lifecycle}"
}

object TestImplementation {
    const val junit = "junit:junit:${Versions.juint}"
}

object AndroidTestImplementation {
    const val junit = "androidx.test.ext:junit:${Versions.extJunit}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
    const val junit4 = "androidx.compose.ui:ui-test-junit4:${Versions.jUnit4}"
}

object DebugImplementation {
    const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.uiTooling}"
    const val testManifest = "androidx.compose.ui:ui-test-manifest:${Versions.testManifest}"
}

object Navigation {
    const val composeNavigation =
        "androidx.navigation:navigation-compose:${Versions.composeNavigation}"
}

object Deps {
    const val core = "androidx.core:core-ktx:${Versions.core}"
}

object SplashScreen {
    const val coreSplash = "androidx.core:core-splashscreen:${Versions.splash}"
}

object DaggerHilt {
    const val hiltNavigation = "androidx.hilt:hilt-navigation-compose:${Versions.hilt_navigation}"
    const val daggerHilt = "com.google.dagger:hilt-android:${Versions.hilt_version}"
    const val daggerCompiler =
        "com.google.dagger:dagger-compiler:${Versions.hilt_version}"
    const val daggerHiltCompiler =
        "com.google.dagger:hilt-compiler:${Versions.hilt_version}"
}

object KaKao {
    const val kakao_login = "com.kakao.sdk:v2-user:${Versions.kakao_login}"
}
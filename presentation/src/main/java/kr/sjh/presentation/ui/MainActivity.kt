package kr.sjh.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kr.sjh.presentation.ui.theme.PickUpTheme
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.utill.rememberPickUpAppState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var condition = true

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
            .setKeepOnScreenCondition {
                condition
            }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(backgroundColor.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(backgroundColor.toArgb())
        )
        super.onCreate(savedInstanceState)

        setContent {
            PickUpTheme {
                val mainUiState by viewModel.mainUiState.collectAsStateWithLifecycle(MainUiState.Loading)
                val appState =
                    rememberPickUpAppState()
                PickUpApp(appState = appState, mainUiState = mainUiState) {
                    condition = false
                }

            }
        }
    }
}

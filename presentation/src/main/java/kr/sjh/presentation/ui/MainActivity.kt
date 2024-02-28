package kr.sjh.presentation.ui

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.sjh.presentation.navigation.RootNavGraph
import kr.sjh.presentation.ui.theme.PickUpTheme
import kr.sjh.presentation.ui.theme.backgroundColor

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var condition = true
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            condition
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(backgroundColor.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(backgroundColor.toArgb())
        )
        setContent {
            PickUpTheme {
                val navController = rememberNavController()
                RootNavGraph(
                    navController,
                    onKeepOnScreenCondition = {
                        condition = false
                    }
                )
            }
        }
    }
}

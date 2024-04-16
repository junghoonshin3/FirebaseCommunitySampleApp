package kr.sjh.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.sjh.presentation.navigation.RootNavGraph
import kr.sjh.presentation.ui.theme.PickUpTheme
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.utill.getActivity

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var condition = true
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            condition
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(backgroundColor.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(backgroundColor.toArgb())
        )
        super.onCreate(savedInstanceState)

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

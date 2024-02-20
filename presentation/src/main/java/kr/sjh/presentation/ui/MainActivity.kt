package kr.sjh.presentation.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.kakao.sdk.auth.TokenManagerProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.sjh.presentation.navigation.RootNavGraph
import kr.sjh.presentation.navigation.RootScreen
import kr.sjh.presentation.ui.login.LoginViewModel
import kr.sjh.presentation.ui.theme.PickUpTheme
import kr.sjh.presentation.utill.LoginManager
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var condition = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { condition }
        setContent {
            PickUpTheme {
                val navController = rememberNavController()
                val loginViewModel: LoginViewModel = hiltViewModel()
                val startScreenName by loginViewModel.startScreenName.collectAsState()

                LaunchedEffect(key1 = startScreenName, block = {
                    condition = startScreenName == null
                })

                RootNavGraph(
                    navController, startScreenName ?: RootScreen.Login, loginViewModel
                )
            }
        }
    }
}

package kr.sjh.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kr.sjh.presentation.navigation.Graph
import kr.sjh.presentation.navigation.LoginRouteScreen
import kr.sjh.presentation.ui.login.Logo

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {


    val TAG = "AuthActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Black.toArgb()
        window.navigationBarColor = Color.Black.toArgb()
        setContent {
            SplashRoute(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                navigateToLogin = {
                    startActivity(
                        Intent(Intent.ACTION_VIEW, "petory://login".toUri())
                            .putExtra(
                                "screenName",
                                LoginRouteScreen.Login.route
                            )
                    )
                    finish()
                },
                navigateToMain = {
                    startActivity(Intent(Intent.ACTION_VIEW, "petory://main".toUri()))
                    finish()
                },
                navigateToLoginDetail = {
                    startActivity(
                        Intent(Intent.ACTION_VIEW, "petory://login".toUri())
                            .putExtra(
                                "screenName",
                                LoginRouteScreen.Detail.route
                            )
                    )
                    finish()
                }
            )
        }
    }
}

@Composable
fun SplashRoute(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    navigateToMain: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToLoginDetail: () -> Unit,
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    SplashScreen(
        modifier = modifier,
        authState = authState,
        navigateToMain = navigateToMain,
        navigateToLogin = navigateToLogin,
        navigateToLoginDetail = navigateToLoginDetail
    )
}

@Composable
private fun SplashScreen(
    modifier: Modifier = Modifier,
    authState: AuthUiState,
    navigateToMain: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToLoginDetail: () -> Unit
) {
    when (authState) {
        AuthUiState.AuthToLogin -> navigateToLogin()
        AuthUiState.AuthToMain -> navigateToMain()
        is AuthUiState.Error -> {
            authState.throwable.printStackTrace()
        }

        AuthUiState.Init -> {}
        AuthUiState.AuthToLoginDetail -> navigateToLoginDetail()
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Logo(
            modifier = Modifier.size(192.dp)
        )
    }
}


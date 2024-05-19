package kr.sjh.presentation.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.sjh.presentation.navigation.Graph
import kr.sjh.presentation.ui.login.LoginUiState
import kr.sjh.presentation.ui.login.LoginViewModel
import kr.sjh.presentation.utill.PickUpAppState

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    appState: PickUpAppState,
    onKeepOnScreenCondition: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.loginUiState.collectAsStateWithLifecycle(LoginUiState.Loading)

    LaunchedEffect(key1 = Unit, block = {
        viewModel.onAutoLoginCheck()
    })
    when (uiState) {
        is LoginUiState.Error -> {
            appState.rootNavHostController.navigate(Graph.LoginGraph.route) {
                popUpTo(Graph.SplashGraph.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
            onKeepOnScreenCondition()
        }

        is LoginUiState.Success -> {
            appState.rootNavHostController.navigate(Graph.MainGraph.route) {
                popUpTo(Graph.SplashGraph.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
            onKeepOnScreenCondition()
        }

        LoginUiState.Loading -> {

        }
    }
}
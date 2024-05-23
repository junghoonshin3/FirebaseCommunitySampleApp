package kr.sjh.presentation.ui.splash

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.sjh.presentation.navigation.Graph
import kr.sjh.presentation.ui.login.LoginCheckUiState
import kr.sjh.presentation.ui.login.LoginViewModel
import kr.sjh.presentation.utill.PickUpAppState
import kr.sjh.presentation.utill.getActivity

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    appState: PickUpAppState,
    onKeepOnScreenCondition: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(getActivity())
) {
    val state by viewModel.loginCheckState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit, block = {
        viewModel.tokenExist()
    })

    when (state) {
        is LoginCheckUiState.Error -> {
            Log.d("sjh", "SplashScreen Error")
            appState.rootNavHostController.navigate(Graph.LoginGraph.route) {
                popUpTo(Graph.SplashGraph.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
            onKeepOnScreenCondition()
        }

        LoginCheckUiState.Loading -> {
            Log.d("sjh", "SplashScreen Loading")
        }

        LoginCheckUiState.Success -> {
            Log.d("sjh", "SplashScreen Success")
            appState.rootNavHostController.navigate(Graph.MainGraph.route) {
                popUpTo(Graph.SplashGraph.route) { inclusive = true }
                launchSingleTop = true
            }
            onKeepOnScreenCondition()
        }
    }

    Column(modifier = modifier) {
    }
}
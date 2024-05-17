package kr.sjh.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kr.sjh.presentation.navigation.Graph
import kr.sjh.presentation.navigation.PickUpNavHost
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.PickUpAppState

@Composable
fun PickUpApp(
    appState: PickUpAppState,
    mainUiState: MainUiState,
    onKeepOnScreenCondition: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), contentColor = backgroundColor) {
        when (mainUiState) {
            is MainUiState.Error -> {
                PickUpNavHost(
                    appState = appState,
                    startScreen = Graph.LoginGraph,
                    onKeepOnScreenCondition = onKeepOnScreenCondition,
                )
            }

            MainUiState.Loading -> {
            }

            is MainUiState.Success -> {
                PickUpNavHost(
                    appState = appState,
                    userInfo = userInfo,
                    startScreen = Graph.MainGraph,
                    onKeepOnScreenCondition = onKeepOnScreenCondition,
                )

            }
        }

    }

}
package kr.sjh.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kr.sjh.presentation.navigation.Graph
import kr.sjh.presentation.navigation.PickUpNavHost
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.utill.PickUpAppState

@Composable
fun PickUpApp(
    appState: PickUpAppState,
    onKeepOnScreenCondition: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), contentColor = backgroundColor) {
        PickUpNavHost(
            appState = appState,
            onKeepOnScreenCondition = onKeepOnScreenCondition,
        )
    }
}
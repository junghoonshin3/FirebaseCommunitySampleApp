package kr.sjh.presentation.utill

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kr.sjh.presentation.navigation.MainRouteScreen

@OptIn(ExperimentalLayoutApi::class)
fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }
    if (isFocused) {
        val imeIsVisible = WindowInsets.isImeVisible
        val focusManager = LocalFocusManager.current
        LaunchedEffect(imeIsVisible) {
            if (imeIsVisible) {
                keyboardAppearedSinceLastFocused = true
            } else if (keyboardAppearedSinceLastFocused) {
                focusManager.clearFocus()
            }
        }
    }
    onFocusEvent {
        if (isFocused != it.isFocused) {
            isFocused = it.isFocused
            if (isFocused) {
                keyboardAppearedSinceLastFocused = false
            }
        }
    }
}

@Composable
fun rememberPickUpAppState(
    rootNavHostController: NavHostController = rememberNavController(),
    mainNavHostController: NavHostController = rememberNavController(),
): PickUpAppState {
    return remember(
        rootNavHostController,
        mainNavHostController,
    ) {
        PickUpAppState(
            rootNavHostController,
            mainNavHostController,
        )
    }
}

@Stable
class PickUpAppState(
    val rootNavHostController: NavHostController,
    val mainNavHostController: NavHostController,
)

fun NavHostController.navigateMainRoute(screen: MainRouteScreen) {
    navigate(screen.route) {
        popUpTo(screen.route) {
            inclusive = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun getActivity() = LocalContext.current as ComponentActivity
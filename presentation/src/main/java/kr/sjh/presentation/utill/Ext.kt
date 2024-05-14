package kr.sjh.presentation.utill

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.sjh.domain.usecase.login.model.UserInfo
import kr.sjh.presentation.navigation.MainRouteScreen
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

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
fun getActivity() = LocalContext.current as ComponentActivity


@Composable
fun rememberPickUpAppState(
    rootNavHostController: NavHostController = rememberNavController(),
    mainNavHostController: NavHostController = rememberNavController(),
    userInfo: UserInfo? = null
): PickUpAppState {
    return remember(rootNavHostController, mainNavHostController, userInfo) {
        PickUpAppState(rootNavHostController, mainNavHostController, userInfo)
    }
}

@Stable
class PickUpAppState(
    val rootNavHostController: NavHostController,
    val mainNavHostController: NavHostController,
    var userInfo: UserInfo?
) {
//    fun navigateTopLevelScreen(destination: Graph) {
//        when (destination) {
//            Graph.LoginGraph -> rootNavHostController.navigateToLoginGraph()
//            Graph.MainGraph -> TODO()
//        }
//    }
//
//    fun navigate(screen: Screen) {
//        navController.navigate(screen.route)
//    }
//
//    fun navigateWithOptions(screen: Screen, builder: (NavOptionsBuilder.() -> Unit)) {
//        navController.navigate(screen.route, builder)
//    }
//
//    fun popBackStack() {
//        navController.popBackStack()
//    }
}

fun NavHostController.navigateMainRoute(screen: MainRouteScreen) {
    navigate(screen.route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}
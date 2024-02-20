package kr.sjh.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kr.sjh.presentation.ui.chat.ChatScreen
import kr.sjh.presentation.ui.list.ListScreen
import kr.sjh.presentation.ui.login.LoginScreen
import kr.sjh.presentation.ui.login.LoginViewModel
import kr.sjh.presentation.ui.main.MainScreen
import kr.sjh.presentation.ui.setting.SettingScreen

@Composable
fun RootNavGraph(
    navHostController: NavHostController,
    startScreen: RootScreen = RootScreen.Login,
    loginViewModel: LoginViewModel
) {
    NavHost(
        navController = navHostController, route = RootScreen.Root.route,
        startDestination = startScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        showLogin(navHostController, loginViewModel)
        showMain()
    }
}

//
@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(
        modifier = Modifier.padding(10.dp),
        navController = navController,
        startDestination = BottomNavigationScreen.List.route,
        route = RootScreen.Main.route,
        enterTransition = {
            EnterTransition.Companion.None
        },
        exitTransition = { ExitTransition.Companion.None },
        popEnterTransition = { EnterTransition.Companion.None },
        popExitTransition = { ExitTransition.Companion.None }

    ) {
        showList(navController)
        showChat(navController)
        showSetting(navController)
    }
}

//----------------------------------------Composable Screen----------------------------------------------------------------

private fun NavGraphBuilder.showList(
    navController: NavController,
) {
    composable(
        route = BottomNavigationScreen.List.route, enterTransition = null,
        exitTransition = null,
        popExitTransition = null,
        popEnterTransition = null
    ) {
        ListScreen(
            navController,
            Modifier
                .fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.showLogin(
    navController: NavHostController,
    loginViewModel: LoginViewModel
) {
    composable(
        route = RootScreen.Login.route, enterTransition = null,
        exitTransition = null,
        popExitTransition = null,
        popEnterTransition = null
    ) {
        LoginScreen(
            navController,
            Modifier
                .fillMaxSize()
                .background(Color.Black), loginViewModel
        )
    }
}

private fun NavGraphBuilder.showMain(
) {
    composable(
        route = RootScreen.Main.route, enterTransition = null,
        exitTransition = null,
        popExitTransition = null,
        popEnterTransition = null
    ) {
        MainScreen(
            Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.showSetting(
    navController: NavController
) {
    composable(
        route = BottomNavigationScreen.Setting.route, enterTransition = null,
        exitTransition = null,
        popExitTransition = null,
        popEnterTransition = null
    ) {
        SettingScreen(
            navController, Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.showChat(
    navController: NavController,
) {
    composable(
        route = BottomNavigationScreen.Chat.route, enterTransition = null,
        exitTransition = null,
        popExitTransition = null,
        popEnterTransition = null
    ) {
        ChatScreen(
            navController, Modifier.fillMaxSize()
        )
    }
}

@Stable
@Composable
fun NavController.currentScreenAsState(): State<BottomNavigationScreen> {
    val selectedItem =
        remember { mutableStateOf<BottomNavigationScreen>(BottomNavigationScreen.List) }
    DisposableEffect(key1 = this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == BottomNavigationScreen.List.route } -> {
                    selectedItem.value = BottomNavigationScreen.List
                }

                destination.hierarchy.any { it.route == BottomNavigationScreen.Chat.route } -> {
                    selectedItem.value = BottomNavigationScreen.Chat
                }

                destination.hierarchy.any { it.route == BottomNavigationScreen.Setting.route } -> {
                    selectedItem.value = BottomNavigationScreen.Setting
                }
            }
        }
        addOnDestinationChangedListener(listener)
        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }
    return selectedItem
}

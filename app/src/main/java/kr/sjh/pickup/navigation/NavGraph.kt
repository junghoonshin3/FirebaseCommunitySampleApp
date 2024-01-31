package kr.sjh.pickup.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kr.sjh.feature.list.ListScreen
import kr.sjh.feature.setting.SettingScreen

@Composable
fun RootNavGraph(
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = RootScreen.List.route) {
        addListRoute(navController)
        addChatRoute(navController)
        addSettingRoute(navController)
    }
}

//-------------------------------------Route Navigation-------------------------------------------------------------------
fun NavGraphBuilder.addListRoute(
    navController: NavController,
) {
    navigation(
        route = RootScreen.List.route,
        startDestination = LeafScreen.List.route
    ) {
        showList(navController)
    }
}

fun NavGraphBuilder.addChatRoute(
    navController: NavController,
) {
    navigation(
        route = RootScreen.Chat.route,
        startDestination = LeafScreen.Chat.route
    ) {
        showChat(navController)
    }
}

fun NavGraphBuilder.addSettingRoute(
    navController: NavController,
) {
    navigation(
        route = RootScreen.Setting.route,
        startDestination = LeafScreen.Setting.route
    ) {
        showSetting(navController)
    }
}

//----------------------------------------Composable Screen----------------------------------------------------------------
private fun NavGraphBuilder.showList(
    navController: NavController,
) {
    composable(route = LeafScreen.List.route) {
        ListScreen(
            navController, Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.showSetting(
    navController: NavController
) {
    composable(route = LeafScreen.Setting.route) {
        SettingScreen(
            navController, Modifier.fillMaxSize()
        )
    }
}

private fun NavGraphBuilder.showChat(
    navController: NavController,
) {
    composable(route = LeafScreen.Chat.route) {
        ListScreen(
            navController, Modifier.fillMaxSize()
        )
    }
}

@Stable
@Composable
fun NavController.currentScreenAsState(): State<RootScreen> {
    val selectedItem = remember { mutableStateOf<RootScreen>(RootScreen.List) }
    DisposableEffect(key1 = this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == RootScreen.List.route } -> {
                    selectedItem.value = RootScreen.List
                }

                destination.hierarchy.any { it.route == RootScreen.Chat.route } -> {
                    selectedItem.value = RootScreen.Chat
                }

                destination.hierarchy.any { it.route == RootScreen.Setting.route } -> {
                    selectedItem.value = RootScreen.Setting
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

@Stable
@Composable
fun NavController.currentRouteAsState(): State<String?> {
    val selectedItem = remember { mutableStateOf<String?>(null) }
    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedItem.value = destination.route
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }
    return selectedItem
}

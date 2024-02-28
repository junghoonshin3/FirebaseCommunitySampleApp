package kr.sjh.presentation.navigation

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import kr.sjh.presentation.ui.MainViewModel
import kr.sjh.presentation.ui.board.BoardDetailScreen
import kr.sjh.presentation.ui.board.BoardScreen
import kr.sjh.presentation.ui.board.BoardWriteScreen
import kr.sjh.presentation.ui.chat.ChatScreen
import kr.sjh.presentation.ui.login.LoginScreen
import kr.sjh.presentation.ui.main.MainScreen
import kr.sjh.presentation.ui.mypage.MyPageScreen

@Composable
fun RootNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel(),
    onKeepOnScreenCondition: () -> Unit
) {

    val startScreen by mainViewModel.startScreenName.collectAsState()

    LaunchedEffect(key1 = startScreen, block = {
        if (startScreen != null) {
            onKeepOnScreenCondition()
        }
    })

    NavHost(
        navController = navController,
        startDestination = startScreen?.route ?: RootScreen.Login.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        showLogin(navController)
        showMain(logOut = {
            navController.navigateToRootScreen(RootScreen.Login)
        })
    }
}

@Composable
fun MainNavGraph(navController: NavHostController = rememberNavController(), logOut: () -> Unit) {
    NavHost(
        modifier = Modifier,
        navController = navController,
        startDestination = RootScreen.Board.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        addBoard(navController)
        addChat(navController)
        addMyPage(navController, logOut)
    }
}

private fun NavGraphBuilder.showLogin(
    navController: NavHostController,
) {
    composable(
        route = RootScreen.Login.route
    ) {
        LoginScreen(
            navController,
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    }
}

private fun NavGraphBuilder.showMain(logOut: () -> Unit) {
    composable(route = RootScreen.Main.route) {
        MainScreen(Modifier.fillMaxSize(), logOut)
    }
}

//board navigation
private fun NavGraphBuilder.addBoard(navController: NavController) {
    navigation(
        route = RootScreen.Board.route,
        startDestination = LeafScreen.Board.route
    ) {
        showBoard(navController)
        showBoarDetail(navController)
        showBoardWrite(navController, Modifier.fillMaxSize())
    }
}

private fun NavGraphBuilder.showBoard(navController: NavController) {
    composable(route = LeafScreen.Board.route) {
        BoardScreen(navController)
    }
}

private fun NavGraphBuilder.showBoarDetail(navController: NavController) {
    composable(
        route = LeafScreen.BoardDetail.route
    ) {
        BoardDetailScreen(
            navController,
            Modifier
                .fillMaxSize()
        ) {
            navController.navigateUp()
        }
    }
}

private fun NavGraphBuilder.showBoardWrite(navController: NavController, modifier: Modifier) {
    composable(
        route = LeafScreen.BoardWrite.route
    ) {
        BoardWriteScreen(
            navController,
            modifier
        ) {
            navController.navigateUp()
        }
    }
}
//end of board navigation

//chat navigation
private fun NavGraphBuilder.addChat(navController: NavController) {
    navigation(
        route = RootScreen.Chat.route,
        startDestination = LeafScreen.Chat.route
    ) {
        showChat(navController)
    }
}

private fun NavGraphBuilder.showChat(navController: NavController) {
    composable(route = LeafScreen.Chat.route) {
        ChatScreen(navController)
    }
}
//end of chat navigation

//mypage navigation
private fun NavGraphBuilder.addMyPage(navController: NavController, logOut: () -> Unit) {
    navigation(
        route = RootScreen.MyPage.route,
        startDestination = LeafScreen.MyPage.route
    ) {
        showMyPage(navController, logOut)
    }
}

private fun NavGraphBuilder.showMyPage(navController: NavController, logOut: () -> Unit) {
    composable(route = LeafScreen.MyPage.route) {
        MyPageScreen(navController, logOut = logOut)
    }
}
//end of mypage navigation


@Stable
@Composable
fun NavController.currentScreenAsState(): State<RootScreen> {
    val selectedItem =
        remember { mutableStateOf<RootScreen>(RootScreen.Board) }
    DisposableEffect(key1 = this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == RootScreen.Board.route } -> {
                    selectedItem.value = RootScreen.Board
                }

                destination.hierarchy.any { it.route == RootScreen.Chat.route } -> {
                    selectedItem.value = RootScreen.Chat
                }

                destination.hierarchy.any { it.route == RootScreen.MyPage.route } -> {
                    selectedItem.value = RootScreen.MyPage
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


fun NavController.navigateToRootScreen(rootScreen: RootScreen) {
    navigate(rootScreen.route) {
        launchSingleTop = true
        restoreState = true
        Log.d("sjh", "graph.findStartDestination().id : $${graph.findStartDestination().id}")
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}

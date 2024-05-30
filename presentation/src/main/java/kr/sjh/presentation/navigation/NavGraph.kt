package kr.sjh.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import kr.sjh.presentation.ui.board.BoardRoute
import kr.sjh.presentation.ui.board.detail.BoardDetailRoute
import kr.sjh.presentation.ui.board.edit.BoardEditRoute
import kr.sjh.presentation.ui.board.write.BoardWriteRoute
import kr.sjh.presentation.ui.login.LoginRoute
import kr.sjh.presentation.ui.login.detail.LoginDetailScreen
import kr.sjh.presentation.ui.main.MainRoute
import kr.sjh.presentation.ui.splash.SplashScreen
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.utill.PickUpAppState

fun NavGraphBuilder.addNestedLoginGraph(
    appState: PickUpAppState,
) {
    navigation(
        startDestination = LoginRouteScreen.Login.route,
        route = Graph.LoginGraph.route
    ) {

        composable(route = LoginRouteScreen.Login.route) {
            LoginRoute(
                appState = appState,
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable(route = LoginRouteScreen.Detail.route) {
            LoginDetailScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                onBack = {
                    appState.rootNavHostController.popBackStack(
                        LoginRouteScreen.Login.route,
                        inclusive = true
                    )
                },
                onComplete = {
                    appState.rootNavHostController.navigate(Graph.MainGraph.route) {
                        popUpTo(LoginRouteScreen.Detail.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun MainNavGraph(
    modifier: Modifier,
    mainNavController: NavHostController,
    moveBoardDetail: (String) -> Unit,
    moveBoardWrite: () -> Unit,
    paddingValues: PaddingValues
) {
    NavHost(
        modifier = modifier,
        navController = mainNavController,
        route = Graph.MainGraph.route,
        startDestination = MainRouteScreen.Board.route
    ) {
        composable(route = MainRouteScreen.Board.route) {
            BoardRoute(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                moveBoardDetail = moveBoardDetail,
                moveBoardWrite = moveBoardWrite
            )
        }
        composable(MainRouteScreen.Chat.route) {

        }
        composable(MainRouteScreen.MyPage.route) {
        }
    }
}

fun NavGraphBuilder.addNestedBoardGraph(
    appState: PickUpAppState,
) {
    navigation(
        startDestination = BoardRouteScreen.Detail.route,
        route = Graph.BoardGraph.route
    ) {
        composable(route = "${BoardRouteScreen.Detail.route}?postKey={postKey}",
            arguments = listOf(
                navArgument("postKey") {
                    type = NavType.StringType
                    nullable = true
                }
            )) {
            BoardDetailRoute(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                onBack = {
                    appState.rootNavHostController.popBackStack(
                        Graph.MainGraph.route,
                        inclusive = false
                    )
                },
                moveEdit = {
                    appState.rootNavHostController.navigate("${BoardRouteScreen.Edit.route}?postKey=$it")
                },
                onChat = {

                }
            )
        }

        composable(route = BoardRouteScreen.Write.route) {
            BoardWriteRoute(
                modifier = Modifier.fillMaxSize(),
                onBack = {
                    appState.rootNavHostController.popBackStack(
                        Graph.MainGraph.route,
                        inclusive = false
                    )
                },
                onComplete = {
                    appState.rootNavHostController.navigate(
                        "${BoardRouteScreen.Detail.route}?postKey=${it}"
                    ) {
                        popUpTo(BoardRouteScreen.Write.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = "${BoardRouteScreen.Edit.route}?postKey={postKey}",
            arguments = listOf(
                navArgument("postKey") {
                    type = NavType.StringType
                }
            )) {
            BoardEditRoute(
                modifier = Modifier.fillMaxSize(),
                onBack = {
                    appState.rootNavHostController.popBackStack(
                        BoardRouteScreen.Detail.route,
                        inclusive = false
                    )
                })
        }
    }
}

fun NavGraphBuilder.addNestedChatGraph(
    appState: PickUpAppState,
) {
    navigation(
        startDestination = ChatRouteScreen.Detail.route,
        route = Graph.ChatGraph.route
    ) {
        composable(route = ChatRouteScreen.Detail.route) {

        }
    }
}

fun NavGraphBuilder.addNestedMyPageGraph(
    appState: PickUpAppState,
) {
    navigation(
        startDestination = MyPageRouteScreen.Detail.route,
        route = Graph.MyPageGraph.route
    ) {
        composable(route = MyPageRouteScreen.Detail.route) {

        }
    }
}


@Composable
fun PickUpNavHost(
    modifier: Modifier = Modifier,
    appState: PickUpAppState,
//    bottomSheetState: BottomSheetState = rememberBottomSheetStateHolder(Unit),
    onKeepOnScreenCondition: () -> Unit
) {
    val navController = appState.rootNavHostController

    NavHost(
        navController = navController,
        startDestination = Graph.SplashGraph.route,
        modifier = modifier.safeDrawingPadding()
    ) {
        composable(Graph.SplashGraph.route) {
            SplashScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                appState = appState,
                onKeepOnScreenCondition = onKeepOnScreenCondition
            )
        }

        addNestedLoginGraph(appState = appState)
        composable(
            route = Graph.MainGraph.route,
        ) {
            MainRoute(
                modifier = Modifier.fillMaxSize(),
                rootNavController = appState.rootNavHostController,
                mainNavController = appState.mainNavHostController,
            )
        }
        addNestedBoardGraph(appState)
        addNestedChatGraph(appState)
        addNestedMyPageGraph(appState)
    }

}



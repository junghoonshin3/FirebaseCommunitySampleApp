package kr.sjh.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import kr.sjh.domain.usecase.login.model.UserInfo
import kr.sjh.presentation.ui.board.BoardRoute
import kr.sjh.presentation.ui.board.detail.BoardDetailRoute
import kr.sjh.presentation.ui.board.write.BoardWriteRoute
import kr.sjh.presentation.ui.login.LoginRoute
import kr.sjh.presentation.ui.main.MainRoute
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
                modifier = Modifier.fillMaxSize(),
                onMoveScreen = { graph, userInfo ->
                    appState.rootNavHostController.navigate("${graph.route}/${userInfo}")
                }
            )
        }
    }
}


@Composable
fun MainNavGraph(
    modifier: Modifier,
    rootNavController: NavHostController,
    mainNavController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        modifier = modifier,
        navController = mainNavController,
        route = Graph.MainGraph.route,
        startDestination = MainRouteScreen.Board.route
    ) {
        composable(route = MainRouteScreen.Board.route) {
            BoardRoute(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
                moveBoardDetail = { post ->
                    rootNavController.navigate("${BoardRouteScreen.Detail.route}/${post}")
                })
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
        composable(route = "${BoardRouteScreen.Detail.route}/{post}",
            arguments = listOf(
                navArgument("post") {
                    // Make argument type safe
                    type = PostType()
                }
            )) {
            BoardDetailRoute(
                modifier = Modifier
                    .fillMaxSize(),
                onBack = {
                    appState.rootNavHostController.popBackStack()
                },
            )
        }

        composable(route = "${BoardRouteScreen.Write.route}/{userInfo}", arguments = listOf(
            navArgument("userInfo") {
                type = UserInfoType()
            }
        )) {
            BoardWriteRoute(
                modifier = Modifier.fillMaxSize(),
                onBack = {
                    appState.rootNavHostController.popBackStack()
                }
            )
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
    startScreen: Graph,
    onKeepOnScreenCondition: () -> Unit
) {
    val navController = appState.rootNavHostController
    LaunchedEffect(key1 = Unit, block = {
        onKeepOnScreenCondition()
    })
    NavHost(
        navController = navController,
        startDestination = startScreen.route,
        modifier = modifier.safeDrawingPadding()
    ) {
        addNestedLoginGraph(appState)
        composable(
            route = Graph.MainGraph.route
        ) {
            MainRoute(
                modifier = Modifier.fillMaxSize(),
                rootNavController = appState.rootNavHostController,
                mainNavController = appState.mainNavHostController,
                userInfo = appState.userInfo ?: UserInfo()
            )
        }
        addNestedBoardGraph(appState)
        addNestedChatGraph(appState)
        addNestedMyPageGraph(appState)
    }
}



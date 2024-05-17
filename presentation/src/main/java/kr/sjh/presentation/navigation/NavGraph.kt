package kr.sjh.presentation.navigation

import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import kr.sjh.domain.usecase.login.model.Post
import kr.sjh.domain.usecase.login.model.UserInfo
import kr.sjh.presentation.ui.board.BoardRoute
import kr.sjh.presentation.ui.board.detail.BoardDetailRoute
import kr.sjh.presentation.ui.board.detail.BoardDetailViewModel
import kr.sjh.presentation.ui.board.detail.BottomSheetMenu
import kr.sjh.presentation.ui.board.write.BoardWriteRoute
import kr.sjh.presentation.ui.bottomsheet.BottomSheetUiState
import kr.sjh.presentation.ui.bottomsheet.CommonModalBottomSheet
import kr.sjh.presentation.ui.bottomsheet.rememberBottomSheetUiState
import kr.sjh.presentation.ui.login.LoginRoute
import kr.sjh.presentation.ui.main.MainRoute
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
                modifier = Modifier.fillMaxSize(),
                onMoveScreen = { userInfo ->
                    appState.rootNavHostController.navigate(Graph.MainGraph.route) {
                        launchSingleTop = true
                        popUpTo(LoginRouteScreen.Login.route) { inclusive = true }
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
    moveBoardDetail: (Post) -> Unit,
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
                moveBoardDetail = { post ->
                    moveBoardDetail(post)

                },
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
    bottomSheetUiState: BottomSheetUiState,
    userInfo: UserInfo
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
                    .fillMaxSize()
                    .background(backgroundColor),
                onBack = {
                    appState.rootNavHostController.popBackStack(
                        Graph.MainGraph.route,
                        inclusive = false
                    )
                },
                onMoreMenu = { post ->
                    bottomSheetUiState.bottomSheetVisible = true
                }
            )
        }

        composable(route = "${BoardRouteScreen.Write.route}/{userInfo}/{post}", arguments = listOf(
            navArgument("userInfo") {
                type = UserInfoType()
            },
            navArgument("post") {
                nullable = true
                type = PostType()
            }

        )) {
            BoardWriteRoute(
                modifier = Modifier.fillMaxSize(),
                onBack = {
                    appState.rootNavHostController.popBackStack(
                        Graph.MainGraph.route,
                        inclusive = false
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickUpNavHost(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
    appState: PickUpAppState,
    bottomSheetUiState: BottomSheetUiState = rememberBottomSheetUiState(Unit),
    startScreen: Graph,
    onKeepOnScreenCondition: () -> Unit
) {
    val navController = appState.rootNavHostController

    LaunchedEffect(key1 = Unit, block = {
        onKeepOnScreenCondition()
    })

    CommonModalBottomSheet(
        containerColor = backgroundColor,
        showSheet = bottomSheetUiState.bottomSheetVisible,
        onDismissRequest = {
            bottomSheetUiState.bottomSheetVisible = false
        }) {
        bottomSheetUiState.content?.invoke()
    }

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
                userInfo = userInfo
            )
        }
        addNestedBoardGraph(appState, bottomSheetUiState)
        addNestedChatGraph(appState)
        addNestedMyPageGraph(appState)
    }

}



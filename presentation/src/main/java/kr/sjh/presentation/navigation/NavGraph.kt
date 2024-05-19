package kr.sjh.presentation.navigation

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import kr.sjh.model.Post
import kr.sjh.model.UserInfo
import kr.sjh.presentation.ui.board.BoardRoute
import kr.sjh.presentation.ui.board.detail.BoardDetailRoute
import kr.sjh.presentation.ui.board.detail.BoardDetailViewModel
import kr.sjh.presentation.ui.board.detail.BottomSheetMenu
import kr.sjh.presentation.ui.board.write.BoardWriteRoute
import kr.sjh.presentation.ui.bottomsheet.BottomSheetState
import kr.sjh.presentation.ui.bottomsheet.CommonModalBottomSheet
import kr.sjh.presentation.ui.bottomsheet.rememberBottomSheetUiState
import kr.sjh.presentation.ui.login.LoginRoute
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
                modifier = Modifier.fillMaxSize(),
                moveMainScreen = {
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
    bottomSheetState: BottomSheetState,
) {
    navigation(
        startDestination = BoardRouteScreen.Detail.route,
        route = Graph.BoardGraph.route
    ) {
        composable(route = "${BoardRouteScreen.Detail.route}?post={post}",
            arguments = listOf(
                navArgument("post") {
                    // Make argument type safe
                    type = PostType()
                    nullable = true
                }
            )) {
            val viewModel: BoardDetailViewModel = hiltViewModel()

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
                onMoreMenu = { post, userInfo ->
                    bottomSheetState.content = {
                        BottomSheetMenu(onEdit = {
                            appState.rootNavHostController.navigate(BoardRouteScreen.Write.route)
                            bottomSheetState.bottomSheetVisible = false
                        }) {
                            viewModel.deletePost(post, {
                                appState.rootNavHostController.popBackStack(
                                    Graph.MainGraph.route,
                                    inclusive = false
                                )
                            }, {
                                it.printStackTrace()
                            })
                            bottomSheetState.bottomSheetVisible = false

                        }
                    }
                    bottomSheetState.bottomSheetVisible = true
                }
            )
        }

        composable(route = BoardRouteScreen.Write.route) {

            val isEdit = it.arguments?.getBoolean("isEdit", false)

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
    appState: PickUpAppState,
    bottomSheetState: BottomSheetState = rememberBottomSheetUiState(Unit),
    onKeepOnScreenCondition: () -> Unit
) {
    val navController = appState.rootNavHostController

    CommonModalBottomSheet(
        containerColor = backgroundColor,
        showSheet = bottomSheetState.bottomSheetVisible,
        onDismissRequest = {
            bottomSheetState.bottomSheetVisible = false
        }) {
        bottomSheetState.content?.invoke()
    }

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
        addNestedBoardGraph(appState, bottomSheetState)
        addNestedChatGraph(appState)
        addNestedMyPageGraph(appState)
    }

}



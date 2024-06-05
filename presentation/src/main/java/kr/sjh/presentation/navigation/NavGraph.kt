package kr.sjh.presentation.navigation

import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navigation
import kr.sjh.presentation.ui.board.BoardRoute
import kr.sjh.presentation.ui.board.detail.BoardDetailRoute
import kr.sjh.presentation.ui.board.edit.BoardEditRoute
import kr.sjh.presentation.ui.board.write.BoardWriteRoute
import kr.sjh.presentation.ui.login.LoginRoute
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.currentScreenAsState

@Composable
fun LoginNavGraph(
    modifier: Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        route = Graph.LoginGraph.route,
        startDestination = LoginRouteScreen.Login.route
    ) {
        composable(route = LoginRouteScreen.Login.route) {
            LoginRoute(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            )
        }
        composable(route = LoginRouteScreen.Detail.route) {
//            LoginDetailScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun MainNavGraph(
    modifier: Modifier,
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier.padding(paddingValues),
        navController = navController,
        route = Graph.MainGraph.route,
        startDestination = Graph.BoardGraph.route
    ) {
        navigation(
            route = Graph.BoardGraph.route,
            startDestination = BoardRouteScreen.Board.route
        ) {
            composable(route = BoardRouteScreen.Board.route) {
                BoardRoute(
                    modifier = Modifier
                        .fillMaxSize(),
                    moveBoardDetail = {
                        navController.navigate("${BoardRouteScreen.Detail.route}?postKey=${it}")
                    }, moveBoardWrite = {
                        navController.navigate(BoardRouteScreen.Write.route)
                    }
                )
            }
            composable(
                route = "${BoardRouteScreen.Detail.route}?postKey={postKey}"
            ) {
                BoardDetailRoute(
                    modifier = Modifier
                        .fillMaxSize(),
                    onBack = { /*TODO*/ },
                    onChat = { /*TODO*/ },
                    moveEdit = {

                    })
            }
            composable(route = BoardRouteScreen.Edit.route) {
                BoardEditRoute(modifier = Modifier.fillMaxSize()) {

                }
            }
            composable(
                route = BoardRouteScreen.Write.route
            ) {
                BoardWriteRoute(
                    modifier = Modifier
                        .fillMaxSize(),
                    onBack = { },
                    onComplete = {

                    })
            }
        }
        navigation(
            route = Graph.ChatGraph.route,
            startDestination = ChatRouteScreen.Chat.route
        ) {
            composable(route = ChatRouteScreen.Chat.route) {

            }
        }
        navigation(
            route = Graph.MyPageGraph.route,
            startDestination = MyPageRouteScreen.MyPage.route
        ) {
            composable(route = MyPageRouteScreen.MyPage.route) {

            }
        }
    }

}


@Composable
fun BottomNavigation(
    navController: NavController,
    currentSelectedScreen: Graph,
    bottomBarState: Boolean
) {
    val navItems =
        listOf(BottomNavItem.Board, BottomNavItem.Chat, BottomNavItem.MyPage)

    AnimatedVisibility(
        visible = bottomBarState,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        NavigationBar(containerColor = backgroundColor) {
            navItems.forEach { item ->
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(indicatorColor = carrot),
                    alwaysShowLabel = true,
                    label = {
                        Text(text = item.title, color = Color.White, fontSize = 12.sp)
                    },
                    selected = currentSelectedScreen.route == item.rootGraph.route,
                    onClick = {
                        navController.navigate(item.rootGraph.route) {
                            popUpTo(currentSelectedScreen.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            tint = Color.White,
                            imageVector = item.icon,
                            contentDescription = "",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                )
            }

        }
    }

}

fun NavGraphBuilder.addNestedBoardGraph(
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
//                    appState.rootNavHostController.popBackStack(
//                        Graph.MainGraph.route,
//                        inclusive = false
//                    )
                },
                moveEdit = {
//                    appState.rootNavHostController.navigate("${BoardRouteScreen.Edit.route}?postKey=$it")
                },
                onChat = {

                }
            )
        }

        composable(route = BoardRouteScreen.Write.route) {
            BoardWriteRoute(
                modifier = Modifier.fillMaxSize(),
                onBack = {
//                    appState.rootNavHostController.popBackStack(
//                        Graph.MainGraph.route,
//                        inclusive = false
//                    )
                },
                onComplete = {
//                    appState.rootNavHostController.navigate(
//                        "${BoardRouteScreen.Detail.route}?postKey=${it}"
//                    ) {
//                        popUpTo(BoardRouteScreen.Write.route) {
//                            inclusive = true
//                        }
//                    }
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
//                    appState.rootNavHostController.popBackStack(
//                        BoardRouteScreen.Detail.route,
//                        inclusive = false
//                    )
                })
        }
    }
}


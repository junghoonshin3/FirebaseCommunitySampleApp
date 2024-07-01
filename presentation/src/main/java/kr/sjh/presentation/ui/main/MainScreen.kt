package kr.sjh.presentation.ui.main

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import kr.sjh.presentation.navigation.BoardRouteScreen
import kr.sjh.presentation.navigation.BottomNavigation
import kr.sjh.presentation.navigation.ChatRouteScreen
import kr.sjh.presentation.navigation.MyPageRouteScreen
import kr.sjh.presentation.ui.board.BoardRoute
import kr.sjh.presentation.ui.board.BoardViewModel
import kr.sjh.presentation.ui.mypage.MyPageRoute
import kr.sjh.presentation.utill.getActivity

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel(getActivity()),
    moveBoardDetail: (String) -> Unit,
    navigateToLogin: () -> Unit,
    moveBoardWrite: () -> Unit,
) {
    Log.d("sjh", "mainScreen")
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute: () -> String? = {
        navBackStackEntry?.destination?.route
    }

    val currentUser by mainViewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigation(
                onNavChange = {
                    navController.navigate(it) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                currentRoute = currentRoute(),
                imageUrl = currentUser?.profileImageUrl
            )
        }) { paddingValue ->
        NavHost(
            modifier = Modifier
                .fillMaxSize(),
            navController = navController,
            startDestination = BoardRouteScreen.Board.route
        ) {
            composable(route = BoardRouteScreen.Board.route) {
                BoardRoute(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValue),
                    navigateToBoardDetail = moveBoardDetail,
                    navigateToBoardWrite = moveBoardWrite,
                )
            }
            composable(route = ChatRouteScreen.Chat.route) {


            }
            composable(route = MyPageRouteScreen.MyPage.route) {
                MyPageRoute(modifier = Modifier.fillMaxSize(), navigateToLogin = navigateToLogin)
            }
        }
    }
}
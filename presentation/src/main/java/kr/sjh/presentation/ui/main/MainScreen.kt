package kr.sjh.presentation.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kr.sjh.presentation.navigation.BoardRouteScreen
import kr.sjh.presentation.navigation.BottomNavItem
import kr.sjh.presentation.navigation.MainNavGraph
import kr.sjh.presentation.navigation.MainRouteScreen
import kr.sjh.presentation.ui.login.LoginViewModel
import kr.sjh.presentation.utill.navigateMainRoute

@Composable
fun MainRoute(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    mainNavController: NavHostController
) {


    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigation {
                mainNavController.navigateMainRoute(it)
            }
        }
    ) {
        MainNavGraph(
            modifier = Modifier.fillMaxSize(),
            mainNavController = mainNavController,
            paddingValues = it,
            moveBoardDetail = { post ->
                rootNavController.navigate("${BoardRouteScreen.Detail.route}?post=${post}")
            },
            moveBoardWrite = {
                rootNavController.navigate(BoardRouteScreen.Write.route)
            }
        )

    }
}

@Composable
fun BottomNavigation(
    onClick: (MainRouteScreen) -> Unit,
) {
    val navItem =
        listOf(BottomNavItem.Board, BottomNavItem.Chat, BottomNavItem.MyPage)

    var selectedScreen by remember {
        mutableStateOf<MainRouteScreen>(MainRouteScreen.Board)
    }
    NavigationBar {
        navItem.forEach {
            NavigationBarItem(
                alwaysShowLabel = true,
                label = {
                    Text(text = it.title)
                },
                selected = selectedScreen == it.mainScreenRoute,
                onClick = {
                    if (selectedScreen != it.mainScreenRoute) {
                        selectedScreen = it.mainScreenRoute
                    }
                    onClick(it.mainScreenRoute)
                },
                icon = { Icon(imageVector = it.icon, contentDescription = "") })
        }
    }
}
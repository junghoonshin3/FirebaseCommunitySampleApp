package kr.sjh.presentation.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kr.sjh.domain.usecase.login.model.UserInfo
import kr.sjh.presentation.navigation.BoardRouteScreen
import kr.sjh.presentation.navigation.BottomNavItem
import kr.sjh.presentation.navigation.MainNavGraph
import kr.sjh.presentation.navigation.MainRouteScreen
import kr.sjh.presentation.utill.navigateMainRoute

@Composable
fun MainRoute(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    mainNavController: NavHostController,
    userInfo: UserInfo,
    mainViewModel: MainViewModel = hiltViewModel()
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
                rootNavController.navigate("${BoardRouteScreen.Detail.route}/${post}")
            },
            moveBoardWrite = {
                rootNavController.navigate("${BoardRouteScreen.Write.route}/${userInfo}/{}")
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
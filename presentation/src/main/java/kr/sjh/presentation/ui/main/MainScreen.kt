package kr.sjh.presentation.ui.main

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import kr.sjh.domain.usecase.login.model.UserInfo
import kr.sjh.presentation.navigation.BottomNavItem
import kr.sjh.presentation.navigation.LeafScreen
import kr.sjh.presentation.navigation.MainNavGraph
import kr.sjh.presentation.navigation.RootScreen
import kr.sjh.presentation.navigation.currentRouteAsState
import kr.sjh.presentation.navigation.currentScreenAsState
import kr.sjh.presentation.navigation.navigateToRootScreen
import kr.sjh.presentation.ui.MainViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    logOut: () -> Unit
) {
    val navController = rememberNavController()

    //current RootScreen
    val currentSelectedScreen by navController.currentScreenAsState()
    //current LeafScreen
    val currentRoute by navController.currentRouteAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation {
                navController.navigateToRootScreen(it)
            }
        },
        floatingActionButton = {
            if (currentSelectedScreen == RootScreen.Board) {
                ExtendedFloatingActionButton(
                    shape = RoundedCornerShape(30.dp),
                    containerColor = Color.Black,
                    text = { Text(text = "글쓰기", color = Color.White) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "create",
                            tint = Color.White
                        )
                    },
                    onClick = {
                        navController.navigate(LeafScreen.BoardWrite.route)
                    })
            }
        }
    ) {
        MainNavGraph(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it),
            navController = navController,
            mainViewModel = mainViewModel,
            logOut = logOut
        )
    }
}

@Composable
fun BottomNavigation(
    onClick: (RootScreen) -> Unit
) {
    val navItem =
        listOf(BottomNavItem.Board, BottomNavItem.Chat, BottomNavItem.MyPage)

    var selectedScreen by remember {
        mutableStateOf<RootScreen>(RootScreen.Board)
    }

    NavigationBar {
        navItem.forEach {
            NavigationBarItem(
                alwaysShowLabel = true,
                label = {
                    Text(text = it.title)
                },
                selected = it.screenRoute == selectedScreen,
                onClick = {
                    selectedScreen = it.screenRoute
                    onClick(it.screenRoute)
                },
                icon = { Icon(imageVector = it.icon, contentDescription = "") })
        }
    }
}

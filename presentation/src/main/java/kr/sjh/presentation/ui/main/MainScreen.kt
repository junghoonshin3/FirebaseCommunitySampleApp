package kr.sjh.presentation.ui.main

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        },
        floatingActionButton = {
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
                    rootNavController.navigate("${BoardRouteScreen.Write.route}/${userInfo}")
                })
        }
    ) {
        MainNavGraph(
            modifier = Modifier.fillMaxSize(),
            rootNavController = rootNavController,
            mainNavController = mainNavController,
            paddingValues = it,
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

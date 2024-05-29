package kr.sjh.presentation.ui.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kr.sjh.presentation.navigation.BoardRouteScreen
import kr.sjh.presentation.navigation.BottomNavItem
import kr.sjh.presentation.navigation.Graph
import kr.sjh.presentation.navigation.MainNavGraph
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot

@Composable
fun MainRoute(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController,
    mainNavController: NavHostController
) {

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigation(mainNavController)
        }
    ) {
        MainNavGraph(
            modifier = Modifier.fillMaxSize(),
            mainNavController = mainNavController,
            paddingValues = it,
            moveBoardDetail = { postKey ->
                rootNavController.navigate("${BoardRouteScreen.Detail.route}?postKey=${postKey}") {
                    popUpTo(Graph.MainGraph.route)
                    launchSingleTop = true
                }
            },
            moveBoardWrite = {
                rootNavController.navigate(BoardRouteScreen.Write.route) {
                    popUpTo(Graph.MainGraph.route)
                    launchSingleTop = true
                }
            }
        )
    }
}

@Composable
fun BottomNavigation(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination = navBackStackEntry?.destination

    val navItem =
        listOf(BottomNavItem.Board, BottomNavItem.Chat, BottomNavItem.MyPage)

    NavigationBar(containerColor = backgroundColor) {
        navItem.forEach { item ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(indicatorColor = carrot),
                alwaysShowLabel = true,
                label = {
                    Text(text = item.title, color = Color.White, fontSize = 12.sp)
                },
                selected = currentDestination?.route == item.mainScreenRoute.route,
                onClick = {
                    navController.navigate(item.mainScreenRoute.route) {
                        popUpTo(currentDestination?.route.toString()) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = false

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
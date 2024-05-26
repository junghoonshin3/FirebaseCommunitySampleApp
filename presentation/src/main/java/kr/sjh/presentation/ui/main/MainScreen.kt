package kr.sjh.presentation.ui.main

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kr.sjh.presentation.navigation.BoardRouteScreen
import kr.sjh.presentation.navigation.BottomNavItem
import kr.sjh.presentation.navigation.Graph
import kr.sjh.presentation.navigation.MainNavGraph
import kr.sjh.presentation.navigation.MainRouteScreen
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

    LaunchedEffect(key1 = currentDestination, block = {
        Log.d("sjh", "currentDestination:  $currentDestination")
    })

    NavigationBar {
        navItem.forEach { item ->
            NavigationBarItem(
                alwaysShowLabel = true,
                label = {
                    Text(text = item.title)
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
                icon = { Icon(imageVector = item.icon, contentDescription = "") })
        }
    }
}
package kr.sjh.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import kr.sjh.pickup.navigation.BottomNavItem
import kr.sjh.pickup.navigation.RootNavGraph
import kr.sjh.pickup.navigation.RootScreen
import kr.sjh.pickup.navigation.currentRouteAsState
import kr.sjh.pickup.navigation.currentScreenAsState

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val bottomNavList = listOf(BottomNavItem.List, BottomNavItem.Chat, BottomNavItem.Setting)
    val navController = rememberNavController()
    val currentSelectedScreen by navController.currentScreenAsState()
    val currentRoute by navController.currentRouteAsState()
    Scaffold(
        bottomBar = {
            BottomNavigation(bottomNavList, currentSelectedScreen) {
                navController.navigateToRootScreen(it)
            }
        }
    ) {
        Column(modifier = modifier.padding(it)) {
            RootNavGraph(navController = navController)
        }

    }
}

@Composable
fun BottomNavigation(
    navItems: List<BottomNavItem>,
    currentSelectedScreen: RootScreen,
    onClick: (RootScreen) -> Unit
) {
    NavigationBar {
        navItems.forEach {
            NavigationBarItem(
                selected = it.screenRoute == currentSelectedScreen,
                onClick = { onClick(it.screenRoute) },
                icon = { it.icon })
        }
    }
}

private fun NavController.navigateToRootScreen(rootScreen: RootScreen) {
    navigate(rootScreen.route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(Modifier.fillMaxSize())
}


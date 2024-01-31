package kr.sjh.pickup.ui.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
            Log.d("sjh","click")
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
                alwaysShowLabel = true,
                label = {
                    Text(text = it.title)
                },
                selected = it.screenRoute == currentSelectedScreen,
                onClick = { onClick(it.screenRoute) },
                icon = { Icon(imageVector = it.icon, contentDescription = "") })
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


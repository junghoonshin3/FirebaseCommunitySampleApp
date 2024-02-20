package kr.sjh.presentation.ui.main

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import kr.sjh.presentation.navigation.BottomNavItem
import kr.sjh.presentation.navigation.BottomNavigationScreen
import kr.sjh.presentation.navigation.MainNavGraph
import kr.sjh.presentation.navigation.currentScreenAsState
import okhttp3.internal.immutableListOf

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val bottomNavList =
        listOf(BottomNavItem.List, BottomNavItem.Chat, BottomNavItem.Setting)
    val currentSelectedScreen by navController.currentScreenAsState()
    Scaffold(
        bottomBar = {
            BottomNavigation(bottomNavList, currentSelectedScreen) {
                navController.navigateToRootScreen(it)
            }
        }
    ) {
        Column(modifier = modifier.padding(it)) {
            MainNavGraph(navController = navController)
        }
    }
}

@Composable
fun BottomNavigation(
    navItems: List<BottomNavItem>,
    currentSelectedScreen: BottomNavigationScreen,
    onClick: (BottomNavigationScreen) -> Unit
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

private fun NavController.navigateToRootScreen(rootScreen: BottomNavigationScreen) {
    navigate(rootScreen.route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}

package kr.sjh.presentation.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import kr.sjh.presentation.navigation.BottomNavItem
import kr.sjh.presentation.navigation.BottomNavigationScreen
import kr.sjh.presentation.navigation.MainNavGraph
import kr.sjh.presentation.navigation.currentScreenAsState

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    val currentSelectedScreen by navController.currentScreenAsState()

    Scaffold(
        bottomBar = {
            BottomNavigation {
                navController.navigateToRootScreen(it)
            }
        },
        floatingActionButton = {
            if (currentSelectedScreen.route == BottomNavigationScreen.Board.route) {
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
//                        navController.navigate()
                    })
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
    onClick: (BottomNavigationScreen) -> Unit
) {
    val navItem =
        listOf(BottomNavItem.List, BottomNavItem.Chat, BottomNavItem.Setting)

    var selectedScreen by remember {
        mutableStateOf<BottomNavigationScreen>(BottomNavigationScreen.Board)
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

private fun NavController.navigateToRootScreen(rootScreen: BottomNavigationScreen) {
    navigate(rootScreen.route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}

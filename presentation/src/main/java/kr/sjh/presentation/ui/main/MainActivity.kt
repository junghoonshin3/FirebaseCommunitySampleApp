package kr.sjh.presentation.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.sjh.presentation.navigation.BoardRouteScreen
import kr.sjh.presentation.navigation.BottomNavItem
import kr.sjh.presentation.navigation.BottomNavigation
import kr.sjh.presentation.navigation.ChatRouteScreen
import kr.sjh.presentation.navigation.Graph
import kr.sjh.presentation.navigation.MainNavGraph
import kr.sjh.presentation.navigation.MyPageRouteScreen
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.utill.currentRouteAsState
import kr.sjh.presentation.utill.currentScreenAsState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val navController = rememberNavController()

            val currentSelectedScreen by navController.currentScreenAsState()

            val currentRoute by navController.currentRouteAsState()

            val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

            when (currentRoute) {
                BoardRouteScreen.Board.route -> {
                    bottomBarState.value = true
                }

                ChatRouteScreen.Chat.route -> {
                    bottomBarState.value = true
                }

                MyPageRouteScreen.MyPage.route -> {
                    bottomBarState.value = true
                }

                else -> {
                    bottomBarState.value = false
                }
            }
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                bottomBar = {
                    BottomNavigation(navController, currentSelectedScreen, bottomBarState.value)
                }
            ) {
                MainNavGraph(
                    modifier = Modifier
                        .fillMaxSize(),
                    paddingValues = it,
                    navController = navController,
                )
            }
        }
    }
}

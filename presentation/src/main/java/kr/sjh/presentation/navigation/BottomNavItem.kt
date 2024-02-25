package kr.sjh.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String, val icon: ImageVector, val screenRoute: BottomNavigationScreen
) {
    object Chat : BottomNavItem("채팅", Icons.Default.Home, BottomNavigationScreen.Chat)
    object List : BottomNavItem("글 목록", Icons.Default.List, BottomNavigationScreen.Board)
    object Setting : BottomNavItem("설정", Icons.Default.Settings, BottomNavigationScreen.Setting)
}
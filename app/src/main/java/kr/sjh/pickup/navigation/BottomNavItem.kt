package kr.sjh.pickup.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String, val icon: ImageVector, val screenRoute: RootScreen
) {
    object Chat : BottomNavItem("채팅", Icons.Default.Home, RootScreen.Chat)
    object List : BottomNavItem("글 목록", Icons.Default.List, RootScreen.List)
    object Setting : BottomNavItem("설정", Icons.Default.Settings, RootScreen.Setting)
}
package kr.sjh.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String, val icon: ImageVector, val screenRoute: RootScreen
) {
    object Chat : BottomNavItem("채팅", Icons.Default.Home, RootScreen.Chat)
    object Board : BottomNavItem("글 목록", Icons.Default.List, RootScreen.Board)
    object MyPage : BottomNavItem("내 정보", Icons.Default.AccountCircle, RootScreen.MyPage)
}
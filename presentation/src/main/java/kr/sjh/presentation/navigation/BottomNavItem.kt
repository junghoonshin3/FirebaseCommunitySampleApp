package kr.sjh.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import kr.sjh.presentation.R

@Stable
sealed class BottomNavItem(
    val title: String, val iconResource: Int, val screen: Screen
) {
    data object Chat : BottomNavItem("채팅", R.drawable.baseline_chat_24, ChatRouteScreen.Chat)

    data object Board :
        BottomNavItem("글 목록", R.drawable.baseline_format_list_numbered_24, BoardRouteScreen.Board)


    data object MyPage :
        BottomNavItem("내 정보", R.drawable.baseline_person_24, MyPageRouteScreen.MyPage)
}
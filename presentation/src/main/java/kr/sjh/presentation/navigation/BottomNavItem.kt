package kr.sjh.presentation.navigation

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import kr.sjh.presentation.R

@Stable
sealed class BottomNavItem(
    val title: String, val iconResource: Int, val screen: RootScreen
) {
    data object Chat : BottomNavItem("채팅", R.drawable.baseline_chat_24, RootScreen.Chat)

    data object Board :
        BottomNavItem("글 목록", R.drawable.baseline_format_list_numbered_24, RootScreen.Board)


    data object MyPage : BottomNavItem("내 정보", R.drawable.baseline_person_24, RootScreen.MyPage)
}
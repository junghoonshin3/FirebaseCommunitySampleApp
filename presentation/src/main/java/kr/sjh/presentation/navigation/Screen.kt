package kr.sjh.presentation.navigation

import androidx.compose.runtime.Stable

@Stable
sealed class RootScreen(val route: String) {
    data object Board : RootScreen("board_root")
    data object Chat : RootScreen("chat_root")
    data object MyPage : RootScreen("mypage_root")
}

sealed class LeafScreen(val route: String) {
    data object Login : LeafScreen("login_screen")
    data object LoginDetail : LeafScreen("login_detail_screen")
    data object Board : LeafScreen("board_screen")
    data object BoardWrite : LeafScreen("board_write_screen")
    data object BoardEdit : LeafScreen("board_edit_screen")
    data object BoardDetail : LeafScreen("board_detail_screen")
    data object Chat : LeafScreen("chat_screen")

    data object ChatDetail : LeafScreen("chat_detail_screen")
    data object MyPage : LeafScreen("mypage_screen")
}
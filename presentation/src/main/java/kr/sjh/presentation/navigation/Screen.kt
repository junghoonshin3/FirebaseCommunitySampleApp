package kr.sjh.presentation.navigation

sealed class RootScreen(val route: String) {
    data object Main : RootScreen(route = "main_root")
    data object Login : RootScreen(route = "login_root")
    data object Board : RootScreen(route = "board_root")

    data object MyPage : RootScreen(route = "my_page_root")

    data object Chat : RootScreen(route = "chat_root")
}

//하단 Navigation Screen
sealed class LeafScreen(
    val route: String,
) {
    data object BoardWrite : LeafScreen("write")
    data object Chat : LeafScreen("chat")
    data object MyPage : LeafScreen("my_page")
    data object Board : LeafScreen("board")
    data object BoardDetail : LeafScreen("board_detail")
}
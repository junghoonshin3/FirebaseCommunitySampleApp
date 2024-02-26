package kr.sjh.presentation.navigation

sealed class RootScreen(val route: String) {
    object Main : RootScreen(route = "main_root")
    object Login : RootScreen(route = "login_root")

    object Board : RootScreen(route = "board_root")

    object MyPage : RootScreen(route = "my_page_root")

    object Chat : RootScreen(route = "chat_root")
}

//하단 Navigation Screen
sealed class LeafScreen(
    val route: String,
) {
    //    object Main : LeafScreen("main")
    object Chat : LeafScreen("chat")
    object MyPage : LeafScreen("my_page")
    object Board : LeafScreen("board")
    object BoardDetail : LeafScreen("board_detail")
}
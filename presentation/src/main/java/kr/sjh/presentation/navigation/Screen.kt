package kr.sjh.presentation.navigation

import androidx.compose.runtime.Stable

@Stable
sealed class Graph(val route: String) {
    data object LoginGraph : Graph("loginGraph")
    data object MainGraph : Graph("mainGraph")
}

@Stable
sealed class Screen(val route: String)

@Stable
sealed class LoginRouteScreen(route: String) : Screen(route) {
    data object Login : LoginRouteScreen("login")

    data object Detail : LoginRouteScreen("login_detail")
}

@Stable
sealed class BoardRouteScreen(route: String) : Screen(route) {
    data object Board : BoardRouteScreen("board")
    data object Detail : BoardRouteScreen("board_detail")

    data object Write : BoardRouteScreen("board_write")

    data object Edit : BoardRouteScreen("board_edit")
}

@Stable
sealed class ChatRouteScreen(route: String) : Screen(route) {
    data object Chat : ChatRouteScreen("chat")
    data object Detail : ChatRouteScreen("chat_detail")
}

@Stable
sealed class MyPageRouteScreen(route: String) : Screen(route) {
    data object MyPage : MyPageRouteScreen("myPage")
    data object Detail : MyPageRouteScreen("myPage_detail")
}

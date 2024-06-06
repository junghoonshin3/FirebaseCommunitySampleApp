package kr.sjh.presentation.navigation

sealed class Graph(val route: String) {
    data object LoginGraph : Graph("loginGraph")
    data object MainGraph : Graph("mainGraph")
}

sealed class Screen(val route: String)

sealed class LoginRouteScreen(route: String) : Screen(route) {
    data object Login : LoginRouteScreen("login")

    data object Detail : LoginRouteScreen("login_detail")
}

sealed class BoardRouteScreen(route: String) : Screen(route) {
    data object Board : BoardRouteScreen("board")
    data object Detail : BoardRouteScreen("board_detail")

    data object Write : BoardRouteScreen("board_write")

    data object Edit : BoardRouteScreen("board_edit")
}

sealed class ChatRouteScreen(route: String) : Screen(route) {
    data object Chat : ChatRouteScreen("chat")
    data object Detail : ChatRouteScreen("chat_detail")
}

sealed class MyPageRouteScreen(route: String) : Screen(route) {
    data object MyPage : MyPageRouteScreen("myPage")
    data object Detail : MyPageRouteScreen("myPage_detail")
}

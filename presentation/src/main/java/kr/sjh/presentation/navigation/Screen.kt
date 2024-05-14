package kr.sjh.presentation.navigation

import kr.sjh.domain.usecase.login.model.UserInfo

//하단 Navigation Screen
//sealed class LeafScreen(
//    val route: String,
//) {
//    data object BoardWrite : LeafScreen("write")
//    data object Chat : LeafScreen("chat")
//    data object MyPage : LeafScreen("my_page")
//    data object Board : LeafScreen("board")
//    data object BoardDetail : LeafScreen("board_detail")
//}

sealed class Graph(val route: String) {
    data object LoginGraph : Graph("loginGraph")
    data object MainGraph : Graph("mainGraph")

    data object BoardGraph : Graph("boardGraph")

    data object ChatGraph : Graph("chatGraph")

    data object MyPageGraph : Graph("myPageGraph")
}

sealed class LoginRouteScreen(val route: String) {
    data object Login : LoginRouteScreen("login")
}

sealed class MainRouteScreen(val route: String) {
    data object Board : MainRouteScreen("board")

    data object Chat : MainRouteScreen("chat")

    data object MyPage : MainRouteScreen("myPage")
}

sealed class BoardRouteScreen(val route: String) {
    data object Detail : MainRouteScreen("board_detail")

    data object Write : MainRouteScreen("board_write")
}

sealed class ChatRouteScreen(val route: String) {
    data object Detail : ChatRouteScreen("chat_detail")
}

sealed class MyPageRouteScreen(val route: String) {
    data object Detail : MyPageRouteScreen("myPage_detail")

}

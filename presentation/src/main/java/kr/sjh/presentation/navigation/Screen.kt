package kr.sjh.presentation.navigation

sealed class RootScreen(val route: String) {
    object Root : RootScreen(route = "root")
    object Main : RootScreen(route = "main_root")
    object Login : RootScreen(route = "login_root")
}

//하단 Navigation Screen
sealed class BottomNavigationScreen(
    val route: String,
) {
    object Chat : BottomNavigationScreen("chat")
    object Setting : BottomNavigationScreen("setting")
    object Board : BottomNavigationScreen("board")
}
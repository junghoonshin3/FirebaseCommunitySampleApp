package kr.sjh.pickup.navigation

sealed class RootScreen(val route: String) {
    object Chat : RootScreen(route = "chat_root")
    object Setting : RootScreen(route = "setting_root")
    object List : RootScreen(route = "list_root")
}

sealed class LeafScreen(
    val route: String,
) {
    object Chat : LeafScreen("chat")
    object Setting : LeafScreen("setting")
    object List : LeafScreen("list")
}
package kr.sjh.presentation.navigation

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clipScrollableContainer
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreInterceptKeyBeforeSoftKeyboard
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navigation
import kr.sjh.domain.usecase.login.model.Post
import kr.sjh.presentation.ui.MainViewModel
import kr.sjh.presentation.ui.board.BoardScreen
import kr.sjh.presentation.ui.board.detail.BoardDetailScreen
import kr.sjh.presentation.ui.board.write.BoardWriteScreen
import kr.sjh.presentation.ui.board.write.BoardWriteViewModel
import kr.sjh.presentation.ui.chat.ChatScreen
import kr.sjh.presentation.ui.login.LoginScreen
import kr.sjh.presentation.ui.main.MainScreen
import kr.sjh.presentation.ui.mypage.MyPageScreen
import kr.sjh.presentation.utill.getActivity

@Composable
fun RootNavGraph(
    navController: NavHostController,
    onKeepOnScreenCondition: () -> Unit
) {
    val mainViewModel: MainViewModel = hiltViewModel(getActivity())

    val startScreen by mainViewModel.startScreenName.collectAsState()

    LaunchedEffect(key1 = startScreen, block = {
        if (startScreen != null) {
            onKeepOnScreenCondition()
        }
    })

    NavHost(
        navController = navController,
        startDestination = startScreen?.route ?: RootScreen.Login.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {

        showLogin(navController, mainViewModel)
        showMain(navController, mainViewModel, logOut = {
            mainViewModel.logOut {
                navController.navigateToRootScreen(RootScreen.Login)
            }
        }
        )
    }
}

@Composable
fun MainNavGraph(
    modifier: Modifier,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    logOut: () -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = RootScreen.Board.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {

        addBoard(navController, mainViewModel)
        addChat(navController, mainViewModel)
        addMyPage(navController, logOut)
    }
}

private fun NavGraphBuilder.showLogin(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    composable(
        route = RootScreen.Login.route
    ) {
        LoginScreen(
            navController,
            Modifier
                .fillMaxSize()
                .background(Color.Black),
            mainViewModel
        )
    }
}

private fun NavGraphBuilder.showMain(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    logOut: () -> Unit
) {
    composable(route = RootScreen.Main.route) { entry ->
        MainScreen(Modifier.fillMaxSize(), mainViewModel, logOut)
    }
}

//board navigation
private fun NavGraphBuilder.addBoard(
    navController: NavController,
    mainViewModel: MainViewModel,
) {
    navigation(
        route = RootScreen.Board.route,
        startDestination = LeafScreen.Board.route
    ) {
        showBoard(navController)
        showBoarDetail(navController, mainViewModel)
        showBoardWrite(
            navController = navController,
            mainViewModel = mainViewModel,
            onBack = {
                navController.navigateUp()
            }
        )
    }
}

private fun NavGraphBuilder.showBoard(
    navController: NavController,
//    boardViewModel: BoardViewModel
) {
    composable(route = LeafScreen.Board.route) {
        BoardScreen(navController)
    }
}

private fun NavGraphBuilder.showBoarDetail(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    dialog(
        route = "${LeafScreen.BoardDetail.route}/{post}",
        arguments = listOf(navArgument("post") { type = PostType() }),
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        val post = it.arguments?.getParcelable("post") ?: Post()

        val userInfo by mainViewModel.userInfo.collectAsState()

        BoardDetailScreen(
            navController,
            Modifier
                .fillMaxSize(),
            post,
            userInfo = userInfo,
            mainViewModel = mainViewModel
        ) {
            navController.navigateUp()
        }
    }
}

private fun NavGraphBuilder.showBoardWrite(
    navController: NavController,
    mainViewModel: MainViewModel,
    onBack: () -> Unit
) {
    dialog(
        route = LeafScreen.BoardWrite.route,
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        val boardWriteViewModel: BoardWriteViewModel = hiltViewModel()
        val userInfo by mainViewModel.userInfo.collectAsState()
        val scrollState= rememberScrollState()
        BoardWriteScreen(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            boardWriteViewModel = boardWriteViewModel,
            onPost = {
                userInfo?.let {
                    boardWriteViewModel.createPost(it)
                    mainViewModel.updateUserInfo(
                        it.copy(
                            postCount = it.postCount.plus(1)
                        )
                    )
                }
                navController.navigateUp()
            },
            onBack = onBack
        )
    }
}
//end of board navigation

//chat navigation
private fun NavGraphBuilder.addChat(navController: NavController, mainViewModel: MainViewModel) {
    navigation(
        route = RootScreen.Chat.route,
        startDestination = LeafScreen.Chat.route
    ) {
        showChat(navController, mainViewModel)
    }
}

private fun NavGraphBuilder.showChat(navController: NavController, mainViewModel: MainViewModel) {
    composable(route = LeafScreen.Chat.route) {
        val userInfo by mainViewModel.userInfo.collectAsState()
        ChatScreen(
            modifier = Modifier.fillMaxSize(),
            userInfo = userInfo,
            navController = navController
        )
    }
}
//end of chat navigation

//mypage navigation
private fun NavGraphBuilder.addMyPage(navController: NavController, logOut: () -> Unit) {
    navigation(
        route = RootScreen.MyPage.route,
        startDestination = LeafScreen.MyPage.route
    ) {
        showMyPage(navController, logOut)
    }
}

private fun NavGraphBuilder.showMyPage(navController: NavController, logOut: () -> Unit) {
    composable(route = LeafScreen.MyPage.route) {
        MyPageScreen(navController, logOut = logOut)
    }
}
//end of mypage navigation


@Stable
@Composable
fun NavController.currentScreenAsState(): State<RootScreen> {
    val selectedItem =
        remember { mutableStateOf<RootScreen>(RootScreen.Board) }
    DisposableEffect(key1 = this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == RootScreen.Board.route } -> {
                    selectedItem.value = RootScreen.Board
                }

                destination.hierarchy.any { it.route == RootScreen.Chat.route } -> {
                    selectedItem.value = RootScreen.Chat
                }

                destination.hierarchy.any { it.route == RootScreen.MyPage.route } -> {
                    selectedItem.value = RootScreen.MyPage
                }
            }
        }
        addOnDestinationChangedListener(listener)
        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }
    return selectedItem
}

@Stable
@Composable
fun NavController.currentRouteAsState(): State<String?> {
    val selectedItem = remember { mutableStateOf<String?>(null) }
    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedItem.value = destination.route
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }
    return selectedItem
}


fun NavController.navigateToRootScreen(rootScreen: RootScreen) {
    navigate(rootScreen.route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}


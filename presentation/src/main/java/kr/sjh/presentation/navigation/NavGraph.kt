package kr.sjh.presentation.navigation

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.sjh.presentation.ui.board.BoardRoute
import kr.sjh.presentation.ui.board.detail.BoardDetailRoute
import kr.sjh.presentation.ui.board.edit.BoardEditRoute
import kr.sjh.presentation.ui.board.write.BoardWriteRoute
import kr.sjh.presentation.ui.chat.ChatDetailRoute
import kr.sjh.presentation.ui.chat.ChatRoute
import kr.sjh.presentation.ui.login.LoginActivity
import kr.sjh.presentation.ui.login.LoginRoute
import kr.sjh.presentation.ui.login.LoginViewModel
import kr.sjh.presentation.ui.login.detail.LoginDetailRoute
import kr.sjh.presentation.ui.main.MainActivity
import kr.sjh.presentation.ui.main.MainViewModel
import kr.sjh.presentation.ui.mypage.MyPageRoute
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.currentScreenAsState
import kr.sjh.presentation.utill.navigateToRootScreen
import kr.sjh.presentation.utill.toEncodingURL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun LoginNavGraph(
    modifier: Modifier,
    startScreen: String,
    navController: NavHostController = rememberNavController(),
) {
    val activity = LocalContext.current as LoginActivity

    NavHost(
        navController = navController, modifier = modifier, startDestination = startScreen
    ) {
        composable(
            route = LeafScreen.Login.route
        ) {
            LoginRoute(navigateToMain = {
                activity.startActivity(
                    Intent(Intent.ACTION_VIEW, "petory://main".toUri())
                )
                activity.finish()
            }, navigateToLoginDetail = {
                navController.navigate(LeafScreen.LoginDetail.route) {
                    popUpTo(LeafScreen.Login.route) {
                        inclusive = true
                    }
                }
            })
        }
        composable(
            route = LeafScreen.LoginDetail.route
        ) {
            LoginDetailRoute(navigateToMain = {
                activity.startActivity(
                    Intent(Intent.ACTION_VIEW, "petory://main".toUri())
                )
                activity.finish()
            }, onBack = {
                navController.navigate(LeafScreen.Login.route) {
                    popUpTo(LeafScreen.LoginDetail.route) {
                        inclusive = true
                    }
                }
            })
        }
    }
}

@Composable
fun MainNavGraph(
    modifier: Modifier,
    navController: NavHostController = rememberNavController(),
    activity: MainActivity = LocalContext.current as MainActivity
) {
    val mainViewModel: MainViewModel = hiltViewModel()

    val user by mainViewModel.currentUser.collectAsStateWithLifecycle()

    val currentSelectedScreen by navController.currentScreenAsState()

    val bottomBar: @Composable () -> Unit by remember(currentSelectedScreen, user) {
        mutableStateOf({
            BottomNavigation(
                currentSelectedScreen = currentSelectedScreen,
                navController = navController,
                profileImageUrl = user.profileImageUrl
            )
        })
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = RootScreen.Board.route
    ) {
        navigation(route = RootScreen.Board.route, startDestination = LeafScreen.Board.route) {
            composable(route = LeafScreen.Board.route) {
                BoardRoute(modifier = Modifier.fillMaxSize(), navigateToBoardDetail = {
                    navController.navigate("${LeafScreen.BoardDetail.route}?postKey=$it")
                }, navigateToBoardWrite = {
                    navController.navigate("${LeafScreen.BoardWrite.route}?postKey=$it")
                }, bottomBar = bottomBar
                )
            }
            composable(route = "${LeafScreen.BoardEdit.route}?postKey={postKey}") { backStackEntry ->
                BoardEditRoute(modifier = Modifier.fillMaxSize(), onBack = {
                    navController.navigateUp()
                }, navigateToDetail = {
                    navController.navigate("${LeafScreen.BoardDetail.route}?postKey=$it")
                })
            }
            composable(
                route = LeafScreen.BoardWrite.route
            ) {
                BoardWriteRoute(modifier = Modifier.fillMaxSize(), onBack = {
                    navController.popBackStack(LeafScreen.Board.route, false)
                }, navigateToDetail = { postKey ->
                    navController.navigate("${LeafScreen.BoardDetail.route}?postKey=${postKey}")
                })
            }
            composable(
                route = "${LeafScreen.BoardDetail.route}?postKey={postKey}"
            ) {
                BoardDetailRoute(modifier = Modifier.fillMaxSize(), onBack = {
                    navController.popBackStack(LeafScreen.Board.route, false)
                }, onChat = { roomId, nickName, profileImageUrl ->

                    navController.navigate("${LeafScreen.ChatDetail.route}?roomId=$roomId&nickName=$nickName&profileImageUrl=${profileImageUrl.toEncodingURL()}")
                }, onEdit = {
                    navController.navigate("${LeafScreen.BoardEdit.route}?postKey=$it")
                })
            }
        }

        navigation(route = RootScreen.Chat.route, startDestination = LeafScreen.Chat.route) {
            composable(route = LeafScreen.Chat.route) {
                ChatRoute(
                    bottomBar = bottomBar,
                    navigateToDetail = { roomId, nickName, profileImageUrl ->
                        navController.navigate("${LeafScreen.ChatDetail.route}?roomId=$roomId&nickName=$nickName&profileImageUrl=${profileImageUrl.toEncodingURL()}")
                    })
            }

            composable(route = "${LeafScreen.ChatDetail.route}?roomId={roomId}&nickName={nickName}&profileImageUrl={profileImageUrl}") {
                ChatDetailRoute(onBack = {
                    navController.navigateUp()
                })
            }
        }

        navigation(route = RootScreen.MyPage.route, startDestination = LeafScreen.MyPage.route) {
            composable(route = LeafScreen.MyPage.route) {
                MyPageRoute(modifier = Modifier.fillMaxSize(), bottomBar = bottomBar, logOut = {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, "petory://login".toUri()))
                    activity.finish()
                })
            }
        }


    }

}


@Composable
fun BottomNavigation(
    navController: NavController, currentSelectedScreen: RootScreen, profileImageUrl: String?
) {
    val navItems = persistentListOf(BottomNavItem.Board, BottomNavItem.Chat, BottomNavItem.MyPage)

    NavigationBar(containerColor = backgroundColor) {
        navItems.forEach { item ->
            NavigationBarItem(colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,
                selectedTextColor = carrot,
                unselectedTextColor = Color.White,
            ), alwaysShowLabel = true, label = {
                Text(text = item.title, fontSize = 12.sp)
            }, selected = currentSelectedScreen == item.screen, onClick = {
                navController.navigateToRootScreen(item.screen)
            }, icon = {
                GlideImage(imageOptions = ImageOptions(
                    colorFilter = if (item.screen == RootScreen.MyPage) {
                        null
                    } else if (currentSelectedScreen == item.screen) {
                        ColorFilter.tint(carrot)
                    } else {
                        ColorFilter.tint(Color.White)
                    }
                ), requestOptions = {
                    RequestOptions().override(60).circleCrop()
                }, imageModel = {
                    when (item.screen) {
                        RootScreen.MyPage -> profileImageUrl
                        else -> {
                            item.iconResource
                        }
                    }
                })
            })
        }
    }
}


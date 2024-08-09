package kr.sjh.presentation.navigation

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.collections.immutable.persistentListOf
import kr.sjh.presentation.ui.board.BoardRoute
import kr.sjh.presentation.ui.board.detail.BoardDetailRoute
import kr.sjh.presentation.ui.board.edit.BoardEditRoute
import kr.sjh.presentation.ui.board.write.BoardWriteRoute
import kr.sjh.presentation.ui.chat.BadgeCount
import kr.sjh.presentation.ui.chat.ChatDetailRoute
import kr.sjh.presentation.ui.chat.ChatRoute
import kr.sjh.presentation.ui.login.LoginActivity
import kr.sjh.presentation.ui.login.LoginRoute
import kr.sjh.presentation.ui.login.detail.LoginDetailRoute
import kr.sjh.presentation.ui.main.MainActivity
import kr.sjh.presentation.ui.main.MainViewModel
import kr.sjh.presentation.ui.mypage.MyPageRoute
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.clickableSingle
import kr.sjh.presentation.utill.currentRootScreenAsState
import kr.sjh.presentation.utill.navigateToRootScreen
import kr.sjh.presentation.utill.toEncodingURL

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

    val currentSelectedScreen by navController.currentRootScreenAsState()

    val navItems = persistentListOf(
        BottomNavItem.Board, BottomNavItem.Chat, BottomNavItem.MyPage
    )

    val bottomBar: @Composable () -> Unit by remember {
        mutableStateOf({
            Log.d("bottomBar", "totalUnReadMessageCount >>>>>>>>>> ${user.totalUnReadMessageCount}")
            BottomNavigation(
                content = {
                    navItems.forEach { item ->
                        BottomNavigationItem(
                            screen = item.screen,
                            selected = currentSelectedScreen == item.screen,
                            title = item.title,
                            profileImageUrl = user.profileImageUrl,
                            iconResource = item.iconResource,
                            totalUnReadMessageCount = user.totalUnReadMessageCount,
                            onClick = {
                                navController.navigateToRootScreen(it)
                            },
                            modifier = Modifier.sizeIn(minWidth = 60.dp, minHeight = 60.dp)
                        )
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(backgroundColor)
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
                    navController.navigate("${LeafScreen.ChatDetail.route}?roomId=$roomId&nickName=$nickName&profileImageUrl=${profileImageUrl.toEncodingURL()}") {
                        popUpTo(LeafScreen.BoardDetail.route)
                    }
                }, onEdit = {
                    navController.navigate("${LeafScreen.BoardEdit.route}?postKey=$it")
                })
            }
        }

        navigation(route = RootScreen.Chat.route, startDestination = LeafScreen.Chat.route) {
            composable(route = LeafScreen.Chat.route) {
                ChatRoute(bottomBar = bottomBar,
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

@Stable
@Composable
fun BottomNavigation(
    content: @Composable RowScope.() -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Stable
@Composable
fun BottomNavigationItem(
    screen: RootScreen,
    selected: Boolean,
    title: String,
    profileImageUrl: String,
    iconResource: Int,
    totalUnReadMessageCount: Long,
    onClick: (RootScreen) -> Unit,
    modifier: Modifier = Modifier,
) {

    val selectedColor by remember {
        derivedStateOf {
            if (selected) carrot else Color.White
        }
    }

    ConstraintLayout(modifier = modifier.clickableSingle(enabled = !selected) {
        onClick(screen)
    }) {
        val (icon, badge) = createRefs()
        Column(
            modifier = Modifier.constrainAs(icon) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlideImage(imageOptions = if (screen is RootScreen.MyPage) {
                ImageOptions()
            } else {
                ImageOptions(colorFilter = ColorFilter.tint(selectedColor))
            }, requestOptions = {
                RequestOptions().override(60).circleCrop()
            }, imageModel = {
                if (screen is RootScreen.MyPage) {
                    profileImageUrl
                } else {
                    iconResource
                }
            })
            Text(
                color = selectedColor, text = title
            )
        }

        if (screen is RootScreen.Chat) {
            if (totalUnReadMessageCount > 0)
                BadgeCount(modifier = Modifier
                    .sizeIn(25.dp, 25.dp)
                    .constrainAs(badge) {
                        top.linkTo(parent.top)
                        start.linkTo(icon.end)
                    }
                    .clip(CircleShape)
                    .background(carrot),
                    totalUnReadMessageCount,
                    textSize = 12.sp)
        }
    }
}


package kr.sjh.presentation.navigation

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kr.sjh.domain.error.NotFoundUser
import kr.sjh.presentation.ui.board.detail.BoardDetailRoute
import kr.sjh.presentation.ui.board.edit.BoardEditRoute
import kr.sjh.presentation.ui.board.write.BoardWriteRoute
import kr.sjh.presentation.ui.login.LoginActivity
import kr.sjh.presentation.ui.login.LoginRoute
import kr.sjh.presentation.ui.login.detail.LoginDetailScreen
import kr.sjh.presentation.ui.main.MainScreen
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot

@Composable
fun LoginNavGraph(
    modifier: Modifier,
    navController: NavHostController = rememberNavController()
) {
    val activity = LocalContext.current as LoginActivity
    NavHost(
        navController = navController,
        modifier = modifier,
        route = Graph.LoginGraph.route,
        startDestination = LoginRouteScreen.Login.route
    ) {
        composable(route = LoginRouteScreen.Login.route) {
            LoginRoute(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                onLogin = { userInfo, throwable ->
                    if (userInfo != null) {
                        activity.startActivity(
                            Intent(Intent.ACTION_VIEW, "petory://main".toUri())
                                .putExtra("userInfo", userInfo)
                        )
                        activity.finish()
                    } else if (throwable != null) {
                        when (throwable) {
                            is NotFoundUser -> {
                                navController.navigate(LoginRouteScreen.Detail.route)
                            }

                            else -> {

                            }
                        }
                    }


                }
            )
        }
        composable(route = LoginRouteScreen.Detail.route) {
            LoginDetailScreen(modifier = Modifier.fillMaxSize(),
                onComplete = {
                    activity.startActivity(
                        Intent(Intent.ACTION_VIEW, "petory://main".toUri())
                            .putExtra("userInfo", it)
                    )
                }, onBack = {
                    navController.popBackStack(LoginRouteScreen.Login.route, false)
                })
        }
    }
}

@Composable
fun MainNavGraph(
    modifier: Modifier,
) {
    val mainNavController = rememberNavController()
    val detailNavController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = detailNavController,
        startDestination = Graph.MainGraph.route
    ) {

        composable(route = Graph.MainGraph.route) {
            MainScreen(
                modifier = Modifier
                    .fillMaxSize(),
                navController = mainNavController,
                moveBoardWrite = {
                    detailNavController.navigate(BoardRouteScreen.Write.route)
                }, moveBoardDetail = {
                    detailNavController.navigate("${BoardRouteScreen.Detail.route}?postKey=$it")
                })
        }

        composable(
            route = "${BoardRouteScreen.Detail.route}?postKey={postKey}"
        ) {
            BoardDetailRoute(
                modifier = Modifier
                    .fillMaxSize(),
                onBack = {
                    detailNavController.popBackStack(
                        Graph.MainGraph.route,
                        inclusive = false,
                    )
                },
                onChat = { /*TODO*/ },
                moveEdit = {
                    detailNavController.navigate("${BoardRouteScreen.Edit.route}?postKey=$it")
                })
        }
        composable(route = "${BoardRouteScreen.Edit.route}?postKey={postKey}") {
            BoardEditRoute(
                modifier = Modifier
                    .fillMaxSize(),
                onBack = {
                    detailNavController.popBackStack(
                        BoardRouteScreen.Detail.route,
                        inclusive = false
                    )
                }
            )
        }
        composable(
            route = BoardRouteScreen.Write.route
        ) {
            BoardWriteRoute(
                modifier = Modifier
                    .fillMaxSize(),
                onBack = {
                    detailNavController.popBackStack(
                        Graph.MainGraph.route,
                        inclusive = false,
                    )
                },
                onComplete = {
                    detailNavController.navigate("${BoardRouteScreen.Detail.route}?postKey=${it}")
                })
        }


    }

}


@Composable
fun BottomNavigation(
    navController: NavHostController,
    currentRoute: String?,
    imageUrl: String?
) {
    val navItems = listOf(BottomNavItem.Board, BottomNavItem.Chat, BottomNavItem.MyPage)
    NavigationBar(containerColor = backgroundColor) {
        navItems.forEach { item ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedTextColor = carrot,
                    unselectedTextColor = Color.White,
                ),
                alwaysShowLabel = true,
                label = {
                    Text(text = item.title, fontSize = 12.sp)
                },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    GlideImage(
                        imageOptions = ImageOptions(
                            colorFilter = if (item.screen.route == MyPageRouteScreen.MyPage.route) {
                                null
                            } else if (currentRoute == item.screen.route) {
                                ColorFilter.tint(carrot)
                            } else {
                                ColorFilter.tint(Color.White)
                            }
                        ),
                        requestOptions = {
                            RequestOptions().override(60).circleCrop()
                        },
                        imageModel = {
                            when (item.screen) {
                                MyPageRouteScreen.MyPage -> {
                                    imageUrl ?: item.iconResource
                                }

                                else -> {
                                    item.iconResource
                                }
                            }
                        }
                    )
                }
            )
        }
    }

}



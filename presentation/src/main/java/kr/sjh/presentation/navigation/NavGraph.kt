package kr.sjh.presentation.navigation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.collections.immutable.persistentListOf
import kr.sjh.presentation.ui.board.detail.BoardDetailRoute
import kr.sjh.presentation.ui.board.edit.BoardEditRoute
import kr.sjh.presentation.ui.board.write.BoardWriteRoute
import kr.sjh.presentation.ui.login.LoginActivity
import kr.sjh.presentation.ui.login.LoginRoute
import kr.sjh.presentation.ui.login.detail.LoginDetailRoute
import kr.sjh.presentation.ui.main.MainActivity
import kr.sjh.presentation.ui.main.MainScreen
import kr.sjh.presentation.ui.main.MainViewModel
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.getActivity

@Composable
fun LoginNavGraph(
    modifier: Modifier,
    startScreen: String,
    navController: NavHostController = rememberNavController(),
) {
    val activity = LocalContext.current as LoginActivity
    NavHost(
        navController = navController,
        modifier = modifier,
        route = Graph.LoginGraph.route,
        startDestination = startScreen
    ) {
        composable(route = LoginRouteScreen.Login.route) { backStackEntry ->
            LoginRoute(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                navigateToLoginDetail = {
                    navController.navigate(LoginRouteScreen.Detail.route)
                },
                navigateToMain = {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, "petory://main".toUri()))
                    activity.finish()
                }
            )
        }
        composable(
            route = LoginRouteScreen.Detail.route
        ) {

            LoginDetailRoute(
                modifier = Modifier.fillMaxSize(),
                navigateToMain = {
                    activity.startActivity(
                        Intent(Intent.ACTION_VIEW, "petory://main".toUri())
                    )
                    activity.finish()
                },
                onBack = {
                    navController.popBackStack(LoginRouteScreen.Login.route, false)
                })
        }
    }
}

@Composable
fun MainNavGraph(
    modifier: Modifier,
    mainNavController: NavHostController = rememberNavController(),
    rootNavController: NavHostController = rememberNavController(),
    activity: MainActivity = LocalContext.current as MainActivity
) {
    NavHost(
        modifier = modifier,
        navController = rootNavController,
        startDestination = Graph.MainGraph.route
    ) {

        composable(route = Graph.MainGraph.route) {
            MainScreen(
                modifier = Modifier
                    .fillMaxSize(),
                navController = mainNavController,
                moveBoardWrite = {
                    rootNavController.navigate(BoardRouteScreen.Write.route)
                }, moveBoardDetail = {
                    rootNavController.navigate("${BoardRouteScreen.Detail.route}?postKey=$it")
                }, navigateToLogin = {
                    activity.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "petory://login".toUri()
                        )
                    )
                    activity.finish()
                })
        }

        composable(
            route = "${BoardRouteScreen.Detail.route}?postKey={postKey}"
        ) {
            BoardDetailRoute(
                modifier = Modifier
                    .fillMaxSize(),
                onBack = {
                    rootNavController.popBackStack(
                        Graph.MainGraph.route,
                        inclusive = false,
                    )
                },
                onChat = { /*TODO*/ },
                onEdit = {
                    rootNavController.navigate("${BoardRouteScreen.Edit.route}?postKey=$it")
                })
        }
        composable(route = "${BoardRouteScreen.Edit.route}?postKey={postKey}") { backStackEntry ->
            BoardEditRoute(
                modifier = Modifier
                    .fillMaxSize(),
                onBack = {
                    rootNavController.popBackStack(
                        BoardRouteScreen.Detail.route,
                        inclusive = false
                    )
                },
                navigateToDetail = {
                    rootNavController.navigate("${BoardRouteScreen.Detail.route}?postKey=$it")
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
                    rootNavController.popBackStack(
                        Graph.MainGraph.route,
                        inclusive = false,
                    )
                },
                navigateToDetail = { postKey ->
                    rootNavController.navigate("${BoardRouteScreen.Detail.route}?postKey=${postKey}")
                })
        }


    }

}


@Composable
fun BottomNavigation(
    onNavChange: (String) -> Unit,
    currentRoute: String?,
    imageUrl: String?
) {
    val navItems = persistentListOf(BottomNavItem.Board, BottomNavItem.Chat, BottomNavItem.MyPage)

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
                    onNavChange(item.screen.route)
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


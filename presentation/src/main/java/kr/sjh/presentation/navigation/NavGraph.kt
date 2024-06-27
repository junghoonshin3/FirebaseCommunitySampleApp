package kr.sjh.presentation.navigation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bumptech.glide.request.RequestOptions
import com.google.gson.ExclusionStrategy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kr.sjh.domain.model.PostModel
import kr.sjh.presentation.ui.board.detail.BoardDetailRoute
import kr.sjh.presentation.ui.board.edit.BoardEditRoute
import kr.sjh.presentation.ui.board.write.BoardWriteRoute
import kr.sjh.presentation.ui.login.LoginActivity
import kr.sjh.presentation.ui.login.LoginRoute
import kr.sjh.presentation.ui.login.detail.LoginDetailRoute
import kr.sjh.presentation.ui.main.MainActivity
import kr.sjh.presentation.ui.main.MainScreen
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot

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
) {
    val mainNavController = rememberNavController()
    val detailNavController = rememberNavController()
    val activity = LocalContext.current as MainActivity
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
                    detailNavController.popBackStack(
                        Graph.MainGraph.route,
                        inclusive = false,
                    )
                },
                onChat = { /*TODO*/ },
                onEdit = {
                    detailNavController.navigate("${BoardRouteScreen.Edit.route}?postKey=$it")
                })
        }
        composable(route = "${BoardRouteScreen.Edit.route}?postKey={postKey}") { backStackEntry ->
            BoardEditRoute(
                modifier = Modifier
                    .fillMaxSize(),
                onBack = {
                    detailNavController.popBackStack(
                        BoardRouteScreen.Detail.route,
                        inclusive = false
                    )
                },
                navigateToDetail = {
                    detailNavController.navigate("${BoardRouteScreen.Detail.route}?postKey=$it")
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
                navigateToDetail = { postKey ->
                    Log.d("sjh", "??????????")
                    detailNavController.navigate("${BoardRouteScreen.Detail.route}?postKey=${postKey}")
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

class UrlTypeAdapter : TypeAdapter<String>() {
    override fun write(out: JsonWriter, value: String?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.value(value)
    }

    override fun read(`in`: JsonReader): String {
        return `in`.nextString()
    }
}



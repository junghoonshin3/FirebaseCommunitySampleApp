package kr.sjh.presentation.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.navOptions
import kr.sjh.presentation.R
import kr.sjh.presentation.navigation.RootScreen
import kr.sjh.presentation.navigation.navigateToRootScreen
import kr.sjh.presentation.ui.MainViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
) {
    val isLogin by mainViewModel.isLogin.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = isLogin, block = {
        if (isLogin) {
            navController.navigate(RootScreen.Main.route, navOptions = navOptions {
                popUpTo(RootScreen.Login.route) {
                    inclusive = true
                }
            })
        }
    })

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Logo(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
        )
        Image(
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(300.dp)
                .height(90.dp)
                .padding(10.dp)
                .clickable {
                    mainViewModel.loginForKakao()
                },
            painter = painterResource(id = R.drawable.kakao_login_large_narrow),
            contentDescription = "login"
        )

    }
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.mipmap.app_icon_foreground),
            modifier = modifier,
            contentDescription = ""
        )
    }
}
package kr.sjh.presentation.ui.login

import android.content.Intent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kr.sjh.domain.error.NotFoundUser
import kr.sjh.domain.model.UserInfo
import kr.sjh.presentation.R
import kr.sjh.presentation.navigation.LoginRouteScreen
import kr.sjh.presentation.utill.getActivity


@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(getActivity()),
    onLogin: (UserInfo?, Throwable?) -> Unit
) {
    val context = (LocalContext.current as LoginActivity)
    LoginScreen(
        modifier = modifier,
        onLogin = {
            loginViewModel.kaKaoLogin(context, onLogin)
        }
    )
}

@Composable
private fun LoginScreen(
    modifier: Modifier = Modifier,
    onLogin: () -> Unit
) {
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
                    onLogin()
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
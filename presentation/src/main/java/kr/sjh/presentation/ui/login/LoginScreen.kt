package kr.sjh.presentation.ui.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.sjh.domain.usecase.login.model.UserInfo
import kr.sjh.presentation.R
import kr.sjh.presentation.navigation.Graph


@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    onMoveScreen: (Graph, UserInfo) -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = Unit, block = {
        loginViewModel.loginUiState.collect { loginState ->
            when (loginState) {
                is LoginUiState.Error -> {
                    Log.d("sjh", "Login Error")
                }

                LoginUiState.Loading -> {
                    Log.d("sjh", "Login Loading")
                }

                is LoginUiState.Success -> {
                    Log.d("sjh", "Login Success")
                    val userInfo = loginState.userInfo
                    onMoveScreen(Graph.MainGraph, userInfo)
                }
            }
        }
    })

    LoginScreen(
        modifier = modifier,
        onLogin = {
            loginViewModel.loginForKakao()
        }
    )
}

@Composable
private fun LoginScreen(
    modifier: Modifier = Modifier,
    onLogin: () -> Unit
) {
    Surface(modifier = modifier, color = Color.Black) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
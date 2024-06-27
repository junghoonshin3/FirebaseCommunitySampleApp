package kr.sjh.presentation.ui.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.theme.Roboto_Medium


@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
    navigateToLoginDetail: () -> Unit,
    navigateToMain: () -> Unit
) {

    val loginUiState by loginViewModel.loginUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LoginScreen(
        context = context,
        modifier = modifier,
        loginUiState = loginUiState,
        onLogin = loginViewModel::signIn,
        navigateToLoginDetail = navigateToLoginDetail,
        navigateToMain = navigateToMain
    )

}

@Composable
private fun LoginScreen(
    context: Context,
    modifier: Modifier = Modifier,
    loginUiState: LoginUiState,
    onLogin: (Context) -> Unit,
    navigateToLoginDetail: () -> Unit,
    navigateToMain: () -> Unit
) {

    when (loginUiState) {
        is LoginUiState.Error -> {
            loginUiState.throwable.printStackTrace()
        }

        LoginUiState.Init -> {}

        is LoginUiState.LoginToDetail -> {
            navigateToLoginDetail()
        }

        LoginUiState.LoginToMain -> {
            navigateToMain()
        }

        LoginUiState.Loading -> {
            LoadingDialog()
        }
    }

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
        LoginButton(
            modifier = Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(5.dp))
                .border(1.dp, Color.LightGray)
                .background(Color.White),
            context = context,
            onClick = onLogin
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

@Composable
fun LoginButton(modifier: Modifier = Modifier, context: Context, onClick: (Context) -> Unit) {
    Row(
        modifier = modifier
            .clickable { onClick(context) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.android_light_rd_na),
            contentDescription = "GoogleLogo"
        )
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            fontFamily = Roboto_Medium,
            fontSize = 14.sp,
            color = Color.Black,
            text = "Google 계정으로 로그인"
        )
    }
}
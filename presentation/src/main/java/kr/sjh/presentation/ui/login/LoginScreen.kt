package kr.sjh.presentation.ui.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.theme.Roboto_Medium
import kr.sjh.presentation.utill.clickableSingle


@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToMain: () -> Unit,
    navigateToLoginDetail: () -> Unit
) {
    val activity = LocalContext.current as LoginActivity

    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()

    LoginScreen(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black),
        loginUiState = loginUiState,
        navigateToMain = navigateToMain,
        navigateToLoginDetail = navigateToLoginDetail,
        onLogin = {
            viewModel.signIn(activity)
        })
}

@Composable
private fun LoginScreen(
    modifier: Modifier = Modifier,
    loginUiState: LoginUiState,
    navigateToMain: () -> Unit,
    navigateToLoginDetail: () -> Unit,
    onLogin: () -> Unit,
) {

    LaunchedEffect(key1 = loginUiState) {
        if (loginUiState.destination == "loginToDetail") {
            Log.d("sjh", "loginToDetail")
            navigateToLoginDetail()
        } else if (loginUiState.destination == "loginToMain") {
            Log.d("sjh", "loginToMain")
            navigateToMain()
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
                .background(Color.White), onClick = onLogin
        )
    }

    if (loginUiState.isLoading) {
        LoadingDialog()
    }
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        ContextCompat.getDrawable(
            LocalContext.current, R.mipmap.ic_launcher
        )?.toBitmap()?.asImageBitmap()?.let {
            Image(
                bitmap = it, modifier = modifier, contentDescription = ""
            )
        }

    }
}

@Composable
fun LoginButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(modifier = modifier
        .clickableSingle {
            onClick()
        }
        .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
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
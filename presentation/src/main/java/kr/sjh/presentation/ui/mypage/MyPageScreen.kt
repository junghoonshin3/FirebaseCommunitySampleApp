package kr.sjh.presentation.ui.mypage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun MyPageRoute(
    modifier: Modifier = Modifier,
    viewModel: MyPageScreenViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit
) {
    MyPageScreen(modifier = modifier, logOut = {
        viewModel.logOut()
        navigateToLogin()
    })
}

@Composable
fun MyPageScreen(modifier: Modifier = Modifier, logOut: () -> Unit) {
    Column(modifier = modifier) {
        Button(onClick = logOut) {
            Text(text = "로그아웃")
        }

    }
}
package kr.sjh.presentation.ui.mypage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kr.sjh.presentation.ui.theme.backgroundColor

@Composable
fun MyPageRoute(
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit,
    logOut: () -> Unit,
    viewModel: MyPageScreenViewModel = hiltViewModel()
) {
    Scaffold(bottomBar = bottomBar, containerColor = backgroundColor) {
        MyPageScreen(modifier = modifier.padding(it),
            logOut = {
                viewModel.logOut()
                logOut()
            })
    }

}

@Composable
fun MyPageScreen(modifier: Modifier = Modifier, logOut: () -> Unit) {
    Column(modifier = modifier) {
        Button(onClick = logOut) {
            Text(text = "로그아웃")
        }
    }
}
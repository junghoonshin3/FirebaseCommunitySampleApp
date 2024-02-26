package kr.sjh.presentation.ui.mypage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kr.sjh.presentation.navigation.RootScreen

@Composable
fun MyPageScreen(navController: NavController, modifier: Modifier = Modifier, logOut: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {

        Button(onClick = { logOut() }) {
            Text(text = "로그아웃")
        }

    }
}
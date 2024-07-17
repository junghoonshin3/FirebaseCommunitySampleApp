package kr.sjh.presentation.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage
import kr.sjh.domain.model.UserModel
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.main.MainViewModel
import kr.sjh.presentation.ui.theme.backgroundColor

@Composable
fun ChatRoute(bottomBar: @Composable () -> Unit, navigateToDetail: (String) -> Unit) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val userModel by mainViewModel.currentUser.collectAsStateWithLifecycle()
    Scaffold(bottomBar = bottomBar) {
        ChatScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            userModel = userModel,
            navigateToDetail = navigateToDetail
        )
    }

}

@Composable
private fun ChatScreen(
    userModel: UserModel, modifier: Modifier = Modifier, navigateToDetail: (String) -> Unit
) {
    Surface(modifier = modifier, color = backgroundColor) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(5) {
                ChatList(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(10.dp),
                    profileUrl = userModel.profileImageUrl,
                    nickname = userModel.nickName,
                    onClick = navigateToDetail
                )
            }
        }
    }
}

@Composable
fun ChatList(
    modifier: Modifier = Modifier, profileUrl: String?, nickname: String?, onClick: (String) -> Unit
) {
    Row(modifier = modifier.clickable {
        onClick("")
    }, verticalAlignment = Alignment.CenterVertically) {
        GlideImage(modifier = Modifier.clip(CircleShape), requestOptions = {
            RequestOptions().override(200, 200).centerInside().circleCrop()
        }, imageModel = {
            //대화 상대의 이미지 Url
            profileUrl ?: R.drawable.baseline_face_24
        })
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            verticalArrangement = Arrangement.Center
        ) {
            Text(color = Color.White, fontSize = 18.sp, text = nickname ?: "이름없음")
            Text(color = Color.White, fontSize = 20.sp, text = "안녕하세요.")
        }

    }
}

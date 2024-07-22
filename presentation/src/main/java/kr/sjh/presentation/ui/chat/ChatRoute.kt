package kr.sjh.presentation.ui.chat

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import kr.sjh.presentation.utill.calculationTime

@Composable
fun ChatRoute(
    bottomBar: @Composable () -> Unit, navigateToDetail: (String, String, String) -> Unit
) {
    val chatViewModel: ChatViewModel = hiltViewModel()
    val chatRoomUiState by chatViewModel.chatRooms.collectAsStateWithLifecycle()
    Scaffold(bottomBar = bottomBar) {
        ChatScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            chatRoomUiState = chatRoomUiState,
            navigateToDetail = navigateToDetail
        )
    }

}

@Composable
private fun ChatScreen(
    modifier: Modifier = Modifier,
    chatRoomUiState: ChatRoomUiState,
    navigateToDetail: (String, String, String) -> Unit
) {
    Surface(modifier = modifier, color = backgroundColor) {
        if (chatRoomUiState.rooms.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color.White,
                    text = "대화 내역이 없습니다."
                )
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(chatRoomUiState.rooms) { room ->
                val isInviter = chatRoomUiState.uid == room.inviter.uid
                ChatRoom(modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(10.dp),
                    recentMessage = room.recentMessage,
                    timeStamp = calculationTime(room.timeStamp!!.time),
                    nickname = if (isInviter) room.invitee.nickName else room.inviter.nickName,
                    profileUrl = if (isInviter) room.invitee.profileImageUrl else room.inviter.profileImageUrl,
                    onClick = {
                        navigateToDetail(
                            room.roomId,
                            if (isInviter) room.invitee.nickName else room.inviter.nickName,
                            if (isInviter) room.invitee.profileImageUrl else room.inviter.profileImageUrl
                        )
                    })
            }
        }
    }
}

@Composable
fun ChatRoom(
    modifier: Modifier = Modifier,
    profileUrl: String?,
    nickname: String?,
    recentMessage: String = "",
    timeStamp: String = "",
    onClick: () -> Unit
) {
    Row(modifier = modifier.clickable {
        onClick()
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
            Text(
                color = Color.White,
                fontSize = 20.sp,
                text = recentMessage,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(color = Color.White, fontSize = 16.sp, text = timeStamp)
        }

    }
}

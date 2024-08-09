package kr.sjh.presentation.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage
import kr.sjh.domain.model.ChatRoomModel
import kr.sjh.domain.util.getReceiverUid
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.calculationTime
import kr.sjh.presentation.utill.clickableSingle
import java.util.Date

@Composable
fun ChatRoute(
    bottomBar: @Composable () -> Unit,
    navigateToDetail: (String, String, String) -> Unit,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
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
    navigateToDetail: (String, String, String) -> Unit,
) {

    Surface(modifier = modifier, color = backgroundColor) {
        if (chatRoomUiState.isLoading) {
            LoadingDialog()
        } else if (chatRoomUiState.rooms.isEmpty()) {
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
        } else {
            ChatRoomList(
                chatRoomUiState.uid,
                chatRooms = chatRoomUiState.rooms,
                navigateToDetail = navigateToDetail
            )
        }
    }
}

@Composable
fun ChatRoomList(
    uid: String, chatRooms: List<ChatRoomModel>, navigateToDetail: (String, String, String) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        itemsIndexed(chatRooms, key = { _, room -> room.roomId }) { index, room ->
            val partnerUid = getReceiverUid(room.roomId, uid)
            val partner = room.users[partnerUid] ?: emptyMap()
            val me = room.users[uid] ?: emptyMap()
            val nickname = partner["nickName"].toString()
            val profileUrl = partner["profileImageUrl"].toString()
            val unReadCount = me["unReadMessageCount"] as? Long ?: 0L

            ChatRoom(modifier = Modifier
                .clickableSingle {
                    navigateToDetail(
                        room.roomId, nickname, profileUrl
                    )
                }
                .fillMaxWidth()
                .height(100.dp)
                .padding(10.dp),
                recentMessage = room.recentMessage,
                timeStamp = calculationTime(room.recentMessageTimeStamp?.time ?: Date().time),
                nickname = nickname,
                profileUrl = profileUrl,
                unReadCount = unReadCount)
            if (chatRooms.lastIndex > index) {
                HorizontalDivider(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }
        }
    }
}


@Composable
fun ChatRoom(
    modifier: Modifier = Modifier,
    profileUrl: String,
    nickname: String,
    unReadCount: Long,
    recentMessage: String,
    timeStamp: String = "",
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        GlideImage(modifier = Modifier
            .clip(CircleShape)
            .sizeIn(60.dp, 60.dp), requestOptions = {
            RequestOptions().override(200, 200).centerInside().circleCrop()
        }, imageModel = {
            //대화 상대의 이미지 Url
            profileUrl
        })
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .background(backgroundColor),
            verticalArrangement = Arrangement.Center
        ) {
            Text(color = Color.White, fontSize = 18.sp, text = nickname)
            Text(
                color = Color.White,
                fontSize = 20.sp,
                text = recentMessage,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(color = Color.White, fontSize = 16.sp, text = timeStamp)
        }
        if (unReadCount > 0) {
            BadgeCount(
                Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(carrot), unReadCount
            )
        }
    }
}

@Stable
@Composable
fun BadgeCount(modifier: Modifier = Modifier, count: Long, textSize: TextUnit = 15.sp) {

    val messageCount by remember(count) {
        derivedStateOf {
            if (count > 999) {
                "999+"
            } else {
                "$count"
            }
        }
    }

    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = messageCount,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = textSize
        )
    }
}
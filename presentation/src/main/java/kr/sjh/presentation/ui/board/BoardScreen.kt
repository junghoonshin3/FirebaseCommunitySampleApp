package kr.sjh.presentation.ui.board

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.sjh.domain.usecase.login.model.Post
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.calculationTime

@Composable
fun BoardRoute(
    modifier: Modifier = Modifier,
    moveBoardDetail: (Post) -> Unit,
    boardViewModel: BoardViewModel = hiltViewModel()
) {
    val boardUiState by boardViewModel.posts.collectAsStateWithLifecycle(BoardUiState.Loading)

    BoardScreen(modifier = modifier, boardUiState = boardUiState, moveBoardDetail = moveBoardDetail)
}

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    boardUiState: BoardUiState,
    moveBoardDetail: (Post) -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (boardUiState) {
            BoardUiState.Loading -> {
                CircularProgressIndicator(color = carrot)

            }

            is BoardUiState.Success -> {
                LazyColumn(
                    modifier = modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(boardUiState.list) { index, post ->
                        Post(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clickable {
                                    moveBoardDetail(post)
                                },
                            title = post.title ?: "",
                            nickname = post.nickName ?: "",
                            createAt = post.createdAt ?: 0,
                            readCount = post.readCount,
                            likeCount = post.likeCount
                        )
                        if (index < boardUiState.list.size - 1)
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

            BoardUiState.Empty -> {
                Log.d("sjh", "empty")
            }

            is BoardUiState.Error -> {
                Log.d("sjh", "error")
            }
        }
    }
}

@Composable
fun NoItem() {

}

@Composable
fun Post(
    modifier: Modifier = Modifier,
    title: String,
    nickname: String,
    createAt: Long,
    readCount: Int,
    likeCount: Int
) {
    val minutesAgo by remember {
        mutableStateOf(calculationTime(createAt))
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .width(150.dp)
                .height(150.dp)
                .clip(RoundedCornerShape(20.dp)),
            painter = painterResource(id = R.drawable.test_image),
            contentDescription = ""
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp)
        ) {
            Text(text = title, overflow = TextOverflow.Ellipsis, fontSize = 19.sp, maxLines = 1)
            Text(
                text = nickname,
                color = Color.Gray,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = minutesAgo,
                color = Color.Gray,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = "조회수 $readCount",
                color = Color.Gray,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = "관심 $likeCount",
                color = Color.Gray,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}



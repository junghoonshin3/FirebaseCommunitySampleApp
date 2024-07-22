package kr.sjh.presentation.ui.board

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.landscapist.glide.GlideImage
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.calculationTime
import java.util.Date

@Composable
fun BoardRoute(
    modifier: Modifier = Modifier,
    navigateToBoardDetail: (String) -> Unit,
    navigateToBoardWrite: () -> Unit,
    bottomBar: @Composable () -> Unit,
    boardViewModel: BoardViewModel = hiltViewModel()
) {
    val boardUiState by boardViewModel.postUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit, block = {
        boardViewModel.getPosts()
    })
    Scaffold(bottomBar = bottomBar) {
        BoardScreen(
            modifier = modifier.padding(it),
            boardUiState = boardUiState,
            navigateToBoardDetail = {
                boardViewModel.updatePostCount(it)
                navigateToBoardDetail(it)
            },
            navigateToBoardWrite = navigateToBoardWrite
        )
    }

}

@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    boardUiState: BoardUiState,
    navigateToBoardDetail: (String) -> Unit,
    navigateToBoardWrite: () -> Unit
) {
    Box(modifier = modifier.background(backgroundColor)) {
        when (boardUiState) {
            BoardUiState.Loading -> {
                LoadingDialog()
            }

            is BoardUiState.Success -> {
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .zIndex(1f),
                    shape = RoundedCornerShape(30.dp),
                    containerColor = carrot,
                    text = {
                        Text(
                            text = "글쓰기", color = Color.White
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "create",
                            tint = Color.White
                        )
                    },
                    onClick = navigateToBoardWrite
                )
                if (boardUiState.posts.isEmpty()) {
                    Text(
                        text = "텅! 글쓰기를 해볼까요?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    return@Box
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(boardUiState.posts) { index, post ->
                        PostItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navigateToBoardDetail(post.postKey)
                                },
                            title = post.title,
                            nickname = post.nickName,
                            createAt = post.timeStamp?.time ?: Date().time,
                            readCount = post.readCount,
                            likeCount = post.likeCount,
                            images = post.images
                        )
                        if (index < boardUiState.posts.size - 1)
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

            is BoardUiState.Error -> {
            }

            BoardUiState.Init -> {}
        }
    }
}

@Composable
fun PostItem(
    modifier: Modifier = Modifier,
    title: String,
    images: List<String>,
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
        GlideImage(
            imageModel = {
                if (images.isEmpty()) {
                    R.drawable.baseline_image_24
                } else {
                    images.first()
                }
            },
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(20.dp)),
            failure = {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_image_not_supported_24),
                    contentDescription = ""
                )
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                fontSize = 17.sp,
                maxLines = 1
            )
            Text(
                text = nickname,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp,
                maxLines = 1
            )
            Text(
                text = minutesAgo,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp,
                maxLines = 1
            )
            Text(
                text = "조회수 $readCount",
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp,
                maxLines = 1
            )
            Text(
                text = "관심 $likeCount",
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp,
                maxLines = 1
            )
        }
    }
}



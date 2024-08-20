package kr.sjh.presentation.ui.board.write

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kr.sjh.presentation.ui.board.image.SelectedImages
import kr.sjh.presentation.ui.common.AppTopBar
import kr.sjh.presentation.ui.common.BoardPicture
import kr.sjh.presentation.ui.common.ContentTextField
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot

@Composable
fun BoardWriteRoute(
    modifier: Modifier = Modifier,
    boardWriteViewModel: BoardWriteViewModel = hiltViewModel(),
    onBack: () -> Unit,
    navigateToDetail: (String) -> Unit
) {

    val writeUiState by boardWriteViewModel.writeUiState.collectAsStateWithLifecycle()

    BoardWriteScreen(
        modifier = modifier,
        onPost = {
            boardWriteViewModel.addPost(writeUiState.post)
        },
        writeUiState = writeUiState,
        onBack = onBack,
        updateContent = {
            boardWriteViewModel.updateContent(it)
        },
        updateTitle = {
            boardWriteViewModel.updateTitle(it)
        },
        onPhoto = {
            boardWriteViewModel.setSelectedImages(it)
        },
        onDelete = {
            boardWriteViewModel.removeSelectedImages(it)
        },
        navigateToDetail = navigateToDetail
    )


}

@Composable
fun BoardWriteScreen(
    modifier: Modifier = Modifier,
    writeUiState: WriteUiState,
    updateContent: (String) -> Unit,
    updateTitle: (String) -> Unit,
    onPhoto: (List<String>) -> Unit,
    navigateToDetail: (String) -> Unit,
    onDelete: (String) -> Unit,
    onPost: () -> Unit,
    onBack: () -> Unit,
) {

    val scrollState = rememberScrollState()

    val snackBarState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()


    if (writeUiState.loading) {
        LoadingDialog()
    }

    if (writeUiState.completedPostKey.isNotBlank()) {
        LaunchedEffect(key1 = writeUiState.completedPostKey, block = {
            navigateToDetail(writeUiState.completedPostKey)
        })
    }


    Box(
        modifier = modifier.background(backgroundColor),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppTopBar(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(60.dp),
                title = "음식점 후기글 쓰기",
                buttonTitle = "등록",
                onBack = onBack,
                backIcon = Icons.Default.ArrowBack,
                onClick = onPost
            )

            BoardWriteBody(
                selectedImages = writeUiState.post.images,
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp),
                title = writeUiState.post.title,
                content = writeUiState.post.content,
                updateContent = updateContent,
                updateTitle = updateTitle,
                scrollState = scrollState,
                onDelete = onDelete
            )

            BoardPicture(modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .imePadding(), onPhoto = {
                if ((writeUiState.post.images.size + it.size) > 3) {
                    coroutineScope.launch {
                        snackBarState.showSnackbar(
                            "사진은 최대 3장까지 첨부 할 수 있어요", "확인", duration = SnackbarDuration.Short
                        )
                    }
                } else {
                    onPhoto(it)
                }
            })
        }
        SnackbarHost(
            hostState = snackBarState, modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
fun BoardWriteWarning(
    modifier: Modifier = Modifier, fontSize: TextUnit, fontColor: Color, text: AnnotatedString
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            modifier = Modifier.padding(10.dp), color = fontColor, fontSize = fontSize, text = text
        )
    }
}

@Composable
fun BoardWriteBody(
    modifier: Modifier = Modifier,
    selectedImages: List<String>,
    title: String,
    updateTitle: (String) -> Unit,
    content: String,
    updateContent: (String) -> Unit,
    onDelete: (String) -> Unit,
    scrollState: ScrollState
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.verticalScroll(state = scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BoardWriteWarning(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFFAFB0B1), shape = RoundedCornerShape(10.dp)),
            fontSize = 15.sp,
            fontColor = Color.White,
            text = buildAnnotatedString {
                append("게시판의 성격과 다른 글의 경우 ")
                withStyle(
                    SpanStyle(
                        fontSize = 17.sp, fontWeight = FontWeight.Bold, color = carrot
                    )
                ) {
                    append("삭제 조치 및 계정 이용 ")
                }
                append("정지될 수 있습니다.")
            })
        if (selectedImages.isNotEmpty()) {
            SelectedImages(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                imageUrls = selectedImages,
                onDelete = onDelete
            )
        }
        ContentTextField(singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, autoCorrect = false, imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.moveFocus(FocusDirection.Next)
            }),
            textStyle = TextStyle.Default.copy(
                color = Color.White,
                fontSize = 20.sp,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = { title },
            onTextChanged = { updateTitle(it) },
            placeholder = {
                Text(
                    fontSize = 20.sp, text = "제목을 입력하세요.", color = Color.LightGray
                )
            })
        ContentTextField(
            parentScrollState = scrollState,
            modifier = Modifier.fillMaxWidth(),
            text = { content },
            onTextChanged = { updateContent(it) },
            placeholder = {
                Text(
                    fontSize = 17.sp, text = "방문한 음식점에 대한 정보를 공유해 주세요!", color = Color.LightGray
                )
            },

            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            textStyle = TextStyle.Default.copy(
                color = Color.White,
                fontSize = 17.sp,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, autoCorrect = false, imeAction = ImeAction.Default
            )

        )
    }
}
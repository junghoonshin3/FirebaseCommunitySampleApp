package kr.sjh.presentation.ui.board.write

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.board.image.UploadImages
import kr.sjh.presentation.ui.common.AppTopBar
import kr.sjh.presentation.ui.common.BoardPicture
import kr.sjh.presentation.ui.common.ContentTextField
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.login.LoginViewModel
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.getActivity

@Composable
fun BoardWriteRoute(
    modifier: Modifier = Modifier,
    boardWriteViewModel: BoardWriteViewModel = hiltViewModel(),
//    loginViewModel: LoginViewModel = hiltViewModel(getActivity()),
    onBack: () -> Unit,
    onComplete: (String) -> Unit
) {
    var selectedImages = remember {
        mutableStateListOf<Uri>()
    }

    val scrollState = rememberScrollState()

//    val userInfo by loginViewModel.userInfo.collectAsStateWithLifecycle()

    val uiState by boardWriteViewModel.uiState.collectAsStateWithLifecycle()

    val snackBarState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()

    when (uiState) {
        is WriteUiState.Error -> {
            (uiState as WriteUiState.Error).throwable.printStackTrace()
        }

        WriteUiState.Loading -> {
            LoadingDialog()
        }

        is WriteUiState.Success -> {
            onComplete((uiState as WriteUiState.Success).postKey)
        }

        WriteUiState.Init -> {}
    }
    Box(
        modifier = modifier.background(backgroundColor),
    ) {
        BoardWriteScreen(
            modifier = Modifier.fillMaxSize(),
            onPost = {
//                boardWriteViewModel.createPost(userInfo?.id.toString(), selectedImages)
            },
            selectedImages = selectedImages,
            onBack = onBack,
            scrollState = scrollState,
            title = boardWriteViewModel.title,
            content = boardWriteViewModel.content,
            updateContent = {
                boardWriteViewModel.updateContent(it)
            },
            updateTitle = {
                boardWriteViewModel.updateTitle(it)
            },
            onPhoto = {
                if ((selectedImages.size + it.size) > 3) {
                    coroutineScope.launch {
                        snackBarState.showSnackbar(
                            "사진은 최대 3장까지 첨부 할 수 있어요",
                            "확인",
                            duration = SnackbarDuration.Short
                        )
                    }
                    return@BoardWriteScreen
                }
                selectedImages.addAll(it)
            }
        )

        SnackbarHost(hostState = snackBarState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun BoardWriteScreen(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    selectedImages: MutableList<Uri>,
    scrollState: ScrollState,
    updateContent: (String) -> Unit,
    updateTitle: (String) -> Unit,
    onPhoto: (List<Uri>) -> Unit,
    onPost: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = modifier.imePadding()
    ) {
        AppTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            title = "음식점 후기글 쓰기",
            buttonTitle = "등록",
            onBack = onBack,
            backIcon = Icons.Default.ArrowBack,
            onClick = onPost
        )
        BoardWriteBody(
            selectedImages = selectedImages,
            modifier = Modifier
                .weight(1f)
                .padding(10.dp),
            title = title,
            content = content,
            updateContent = updateContent,
            updateTitle = updateTitle,
            scrollState = scrollState,
            onDelete = {
                selectedImages.removeAt(selectedImages.indexOf(it))
            }
        )
        BoardPicture(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            onPhoto = onPhoto
        )
    }

}


@Composable
fun BoardWriteWarning(
    modifier: Modifier = Modifier,
    fontSize: TextUnit,
    fontColor: Color,
    text: AnnotatedString
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            modifier = Modifier.padding(10.dp),
            color = fontColor,
            fontSize = fontSize,
            text = text
        )
    }
}

@Composable
fun BoardWriteBody(
    modifier: Modifier = Modifier,
    selectedImages: List<Uri>,
    title: String,
    updateTitle: (String) -> Unit,
    content: String,
    updateContent: (String) -> Unit,
    onDelete: (Uri) -> Unit,
    scrollState: ScrollState

) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .verticalScroll(state = scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BoardWriteWarning(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFAFB0B1), shape = RoundedCornerShape(10.dp)),
            fontSize = 15.sp,
            fontColor = Color.White,
            text = buildAnnotatedString {
                append("게시판의 성격과 다른 글의 경우 ")
                withStyle(
                    SpanStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = carrot
                    )
                ) {
                    append("삭제 조치 및 계정 이용 ")
                }
                append("정지될 수 있습니다.")
            }
        )
        if (selectedImages.isNotEmpty()) {
            UploadImages(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                uris = selectedImages,
                onDelete = onDelete
            )
        }
        ContentTextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                autoCorrect = false,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.moveFocus(FocusDirection.Next)
            }),
            textStyle = TextStyle.Default.copy(
                color = Color.White,
                fontSize = 20.sp,

                ),
            modifier = Modifier
                .fillMaxWidth(),
            text = title,
            onTextChanged = { updateTitle(it) },
            placeholder = {
                Text(
                    fontSize = 20.sp,
                    text = "제목을 입력하세요.",
                    color = Color.LightGray
                )
            }
        )
        ContentTextField(
            parentScrollState = scrollState,
            modifier = Modifier
                .fillMaxWidth(),
            text = content,
            onTextChanged = { updateContent(it) },
            placeholder = {
                Text(
                    fontSize = 17.sp,
                    text = "방문한 음식점에 대한 정보를 공유해 주세요!",
                    color = Color.LightGray
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
                keyboardType = KeyboardType.Text,
                autoCorrect = false,
                imeAction = ImeAction.Default
            )

        )
    }
}


@Composable
fun BoardWritePicture(modifier: Modifier, onPhoto: (List<Uri>) -> Unit) {
    val multiplePhotoPickerLauncher = // 갤러리에서 사진 가져오기
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris: List<Uri> ->
            onPhoto(uris)
        }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .background(backgroundColor, RoundedCornerShape(10.dp))
                .clickable {
                    multiplePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                tint = Color.White,
                painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                contentDescription = "",
            )
            Text(text = "사진", color = Color.White)
        }
    }
}

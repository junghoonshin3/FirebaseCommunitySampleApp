package kr.sjh.presentation.ui.board.write

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
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
import com.bumptech.glide.request.RequestOptions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.skydoves.landscapist.glide.GlideImage
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.common.AppTopBar
import kr.sjh.presentation.ui.common.ContentTextField
import kr.sjh.presentation.ui.login.LoginViewModel
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.clearFocusOnKeyboardDismiss
import kr.sjh.presentation.utill.getActivity

@Composable
fun BoardWriteRoute(
    modifier: Modifier = Modifier,
    boardWriteViewModel: BoardWriteViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(getActivity()),
    onBack: () -> Unit
) {
    var selectedPhotos by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    val scrollState = rememberScrollState()

    val userInfo by loginViewModel.userInfo.collectAsStateWithLifecycle()

    BoardWriteScreen(
        modifier = modifier,
        onPost = {
            userInfo?.let {
                boardWriteViewModel.createPost(it)
                onBack()
            }
        },
        onBack = onBack,
        selectedPhotos = selectedPhotos,
        onSelectedPhotos = {
            Log.d("sjh", "picture : ${it.size}")
            selectedPhotos = it
        },
        scrollState = scrollState,
        title = boardWriteViewModel.title,
        content = boardWriteViewModel.content,
        updateContent = {
            boardWriteViewModel.updateContent(it)
        },
        updateTitle = {
            boardWriteViewModel.updateTitle(it)
        }
    )
}

@Composable
fun BoardWriteScreen(
    modifier: Modifier = Modifier,
    selectedPhotos: List<Uri>,
    title: String,
    content: String,
    scrollState: ScrollState,
    onSelectedPhotos: (List<Uri>) -> Unit,
    updateContent: (String) -> Unit,
    updateTitle: (String) -> Unit,
    onPost: () -> Unit,
    onBack: () -> Unit,
) {

    Surface(
        modifier = modifier,
        contentColor = backgroundColor,
        color = backgroundColor
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
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
                modifier = Modifier
                    .weight(1f),
                selectedPhotos = selectedPhotos,
                title = title,
                content = content,
                updateContent = updateContent,
                updateTitle = updateTitle,
                scrollState = scrollState
            )
            BoardWritePicture(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .imePadding(),
                onPhoto = onSelectedPhotos
            )
        }
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
    selectedPhotos: List<Uri> = emptyList(),
    title: String,
    updateTitle: (String) -> Unit,
    content: String,
    updateContent: (String) -> Unit,
    scrollState: ScrollState

) {

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .verticalScroll(state = scrollState)
            .clearFocusOnKeyboardDismiss()
    ) {
        BoardWriteWarning(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(10.dp)
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
        if (selectedPhotos.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp)
            ) {
                items(selectedPhotos) { uri ->
                    GlideImage(
                        imageModel = {
                            uri
                        },
                        requestOptions = {
                            RequestOptions().override(100, 100)
                        }
                    )
                }
            }
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
                .fillMaxWidth()
                .height(90.dp)
                .padding(10.dp),
            text = title,
            onTextChanged = { updateTitle(it) },
            placeholder = {
                Text(fontSize = 20.sp, text = "제목을 입력하세요.", color = Color.LightGray)
            }
        )
        ContentTextField(
            parentScrollState = scrollState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 400.dp)
                .padding(10.dp),
            text = content,
            onTextChanged = { updateContent(it) },
            placeholder = {
                Text(
                    fontSize = 17.sp,
                    text = "방문한 음식점에 대한 정보를 공유해 주세요!추천하는 메뉴나 매장 이용 팁 등을 공유해 주세요!",
                    color = Color.LightGray
                )
            },

            keyboardActions =
            KeyboardActions(onDone = {
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


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BoardWritePicture(modifier: Modifier, onPhoto: (List<Uri>) -> Unit) {
    val mediaPermissionState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    )

    val takePhotoFromAlbumLauncher = // 갤러리에서 사진 가져오기
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val photos = mutableListOf<Uri>()
            if (result.resultCode == Activity.RESULT_OK) {
                val clipData = result.data?.clipData
                if (clipData == null) {
                    //이미지가 한개일 경우
                    val imageUri = result.data?.data!!
                    photos.add(imageUri)
                    onPhoto(photos)
                } else {
                    val size = clipData.itemCount
                    for (i in 0 until size) {
                        val imageUri = clipData.getItemAt(i).uri
                        photos.add(imageUri)
                    }
                    onPhoto(photos)
                }
            } else if (result.resultCode != Activity.RESULT_CANCELED) {
                onPhoto(emptyList())
            } else {
                onPhoto(emptyList())
            }
        }

    val takePhotoFromAlbumIntent =
        Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("image/jpeg", "image/png", "image/bmp", "image/webp")
            )
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
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
                    mediaPermissionState.launchMultiplePermissionRequest()
                    if (mediaPermissionState.allPermissionsGranted) {
                        takePhotoFromAlbumLauncher.launch(takePhotoFromAlbumIntent)
                    }
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

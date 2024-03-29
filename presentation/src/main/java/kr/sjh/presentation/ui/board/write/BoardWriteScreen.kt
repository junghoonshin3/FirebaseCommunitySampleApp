package kr.sjh.presentation.ui.board.write

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
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
import androidx.navigation.NavController
import kr.sjh.domain.usecase.login.model.UserInfo
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.MainViewModel
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.clearFocusOnKeyboardDismiss

@Composable
fun BoardWriteScreen(
    modifier: Modifier = Modifier,
//    navController: NavController,
    boardWriteViewModel: BoardWriteViewModel,
    onPost: () -> Unit,
    onBack: () -> Unit
) {

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
    ) {
        BoardWriteTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            title = "음식점 후기글 쓰기",
            onBack = onBack,
            onPost = onPost
        )
        HorizontalDivider(
            Modifier
                .fillMaxWidth(), 1.dp, color = Color(0xFFC1C7CD)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .background(backgroundColor)

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
            HorizontalDivider(
                Modifier
                    .fillMaxWidth(), 1.dp, color = Color(0xFFC1C7CD)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                TextField(
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(FocusDirection.Next)
                    }),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedPlaceholderColor = Color.Gray,
                        focusedContainerColor = backgroundColor,
                        unfocusedContainerColor = backgroundColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clearFocusOnKeyboardDismiss(),
                    value = boardWriteViewModel.title,
                    onValueChange = { title ->
                        boardWriteViewModel.updateTitle(title)
                    },
                    placeholder = {
                        Text(text = "제목을 입력하세요.")
                    })

                TextField(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        autoCorrect = false,
                        imeAction = ImeAction.Default
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedPlaceholderColor = Color.Gray,
                        focusedContainerColor = backgroundColor,
                        unfocusedContainerColor = backgroundColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clearFocusOnKeyboardDismiss(),
                    value = boardWriteViewModel.content,
                    onValueChange = { content ->
                        boardWriteViewModel.updateContent(content)
                    },
                    placeholder = {
                        Text(
                            text = "방문한 음식점에 대한 정보를 공유해 주세요!추천하는 메뉴나 매장 이용 팁 등을 공유해 주세요!",
                        )
                    })
            }


        }
        HorizontalDivider(
            Modifier
                .fillMaxWidth(), 1.dp, color = Color(0xFFC1C7CD)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(10.dp)
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .background(backgroundColor, RoundedCornerShape(10.dp))
                    .clickable {

                    }, horizontalArrangement = Arrangement.spacedBy(5.dp)
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
fun BoardWriteTopBar(
    modifier: Modifier = Modifier, title: String, onBack: () -> Unit, onPost: () -> Unit
) {
    Row(
        modifier = modifier
            .background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    onBack()
                },
            imageVector = Icons.Default.ArrowBack,
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = "Back"
        )
        Text(text = title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    onPost()
                },
            text = "등록",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
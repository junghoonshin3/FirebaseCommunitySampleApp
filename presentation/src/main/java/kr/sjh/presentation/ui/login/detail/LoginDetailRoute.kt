package kr.sjh.presentation.ui.login.detail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.common.AppTopBar
import kr.sjh.presentation.ui.common.ContentTextField
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.common.ProfileImage

@Composable
fun LoginDetailRoute(
    viewModel: LoginDetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    navigateToMain: () -> Unit
) {
    val context = LocalContext.current

    val uiState by viewModel.loginDetailUiState.collectAsStateWithLifecycle()

    LoginDetailScreen(modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        nickName = uiState.nickName,
        profileImageUrl = uiState.profileImageUrl,
        onTextChange = viewModel::changeNickName,
        onImageEdit = viewModel::onImageEdit,
        navigateToMain = navigateToMain,
        signUp = { viewModel.signUp(context) },
        onBack = {
            viewModel.onBack()
            onBack()
        })

}

@Composable
fun LoginDetailScreen(
    modifier: Modifier = Modifier,
    uiState: LoginDetailUiState,
    nickName: String,
    profileImageUrl: String?,
    navigateToMain: () -> Unit,
    onTextChange: (String) -> Unit,
    signUp: () -> Unit,
    onBack: () -> Unit,
    onImageEdit: (String) -> Unit,
) {

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { selectedImageUri ->
            onImageEdit(selectedImageUri.toString())
        }
    }

    LaunchedEffect(key1 = uiState) {
        if (uiState.isSuccess) {
            navigateToMain()
        }
    }

    if (uiState.isLoading) {
        LoadingDialog()
    }

    Column(modifier = modifier) {
        AppTopBar(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .height(60.dp),
            title = "프로필 수정",
            buttonTitle = "완료",
            backIcon = Icons.Default.ArrowBack,
            onBack = onBack,
            onClick = signUp
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileImage(
                    modifier = Modifier
                        .size(150.dp)
                        .clickable {
                            multiplePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    imageModel = profileImageUrl ?: R.drawable.baseline_image_24,
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "닉네임", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            ContentTextField(singleLine = true,
                modifier = Modifier
                    .border(1.dp, Color.LightGray, shape = RoundedCornerShape(3.dp))
                    .height(50.dp)
                    .fillMaxWidth()
                    .imePadding(),
                text = { nickName },
                onTextChanged = onTextChange,
                textStyle = TextStyle.Default.copy(color = Color.White, fontSize = 20.sp),
                placeholder = {
                    Text(
                        fontSize = 20.sp, text = "닉네임을 입력해주세요", color = Color.LightGray
                    )
                })
        }
    }
}

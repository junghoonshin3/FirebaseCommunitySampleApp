package kr.sjh.presentation.ui.login.detail

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.common.AppTopBar
import kr.sjh.presentation.ui.common.ContentTextField
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.common.ProfileImage

@Composable
fun LoginDetailRoute(
    modifier: Modifier = Modifier,
    detailViewModel: LoginDetailViewModel = hiltViewModel(),
    navigateToMain: () -> Unit,
    onBack: () -> Unit,
) {

    val uiState by detailViewModel.loginDetailUiState.collectAsStateWithLifecycle()

    LoginDetailScreen(
        modifier = modifier,
        nickName = detailViewModel.nickName,
        profileImageUrl = detailViewModel.imageUrl,
        uiState = uiState,
        onTextChange = {
            detailViewModel.changeNickName(it)
        },
        onCreate = {
            detailViewModel.signUp()
        },
        navigateToMain = navigateToMain,
        onImageEdit = {
            Log.d("sjh", "$it")
            detailViewModel.setImageUri(it)
        },
        onBack = onBack
    )


}

@Composable
fun LoginDetailScreen(
    modifier: Modifier = Modifier,
    nickName: String,
    uiState: LoginDetailUiState,
    profileImageUrl: String?,
    onTextChange: (String) -> Unit,
    onCreate: () -> Unit,
    navigateToMain: () -> Unit,
    onImageEdit: (Uri) -> Unit,
    onBack: () -> Unit,
) {
    when (uiState) {
        is LoginDetailUiState.Fail -> {
            uiState.throwable.printStackTrace()
        }

        LoginDetailUiState.Init -> {}
        LoginDetailUiState.Loading -> {
            LoadingDialog()
        }

        LoginDetailUiState.LoginDetailToMain -> {
            navigateToMain()
        }
    }

    Column(modifier = modifier) {
        AppTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            title = "프로필 수정",
            buttonTitle = "완료",
            onBack = onBack,
            onClick = onCreate
        )
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            ProfileImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                imageModel = {
                    profileImageUrl ?: R.drawable.baseline_image_24
                },
                requestOptions = {
                    RequestOptions()
                        .override(350, 350)
                        .circleCrop()
                },
                onImageEdit = onImageEdit
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "닉네임", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            ContentTextField(
                singleLine = true,
                modifier = Modifier
                    .border(1.dp, Color.LightGray, shape = RoundedCornerShape(3.dp))
                    .height(50.dp)
                    .fillMaxWidth()
                    .imePadding(),
                text = nickName,
                onTextChanged = onTextChange,
                textStyle = TextStyle.Default.copy(color = Color.White, fontSize = 20.sp),
                placeholder = {
                    Text(
                        fontSize = 20.sp,
                        text = "닉네임을 입력해주세요",
                        color = Color.LightGray
                    )
                })
        }
    }
}

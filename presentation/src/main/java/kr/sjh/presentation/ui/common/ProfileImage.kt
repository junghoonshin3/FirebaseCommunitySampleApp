package kr.sjh.presentation.ui.common

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage
import kr.sjh.presentation.R

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    imageModel: () -> Any?,
    requestOptions: @Composable () -> RequestOptions,
    onImageEdit: (String) -> Unit
) {
    val imagePick = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri ->
            onImageEdit(selectedImageUri.toString())
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(150.dp)) {
            GlideImage(
                modifier = Modifier
                    .align(Alignment.Center),
                requestOptions = requestOptions,
                imageModel = imageModel,
            )
            Image(
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(35.dp)
                    .clickable {
                        imagePick.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                contentDescription = ""
            )
        }
    }

}
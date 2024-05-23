package kr.sjh.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage
import kr.sjh.presentation.R

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    imageModel: () -> Any?,
    requestOptions: @Composable () -> RequestOptions,
    onImageEdit: () -> Unit
) {
    ConstraintLayout(modifier = modifier) {
        val (image, editImage) = createRefs()
        GlideImage(
            modifier = Modifier
                .constrainAs(image) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .clickable {
                    onImageEdit()
                },
            requestOptions = requestOptions,
            imageModel = imageModel
        )
        Image(
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .size(35.dp)
                .constrainAs(editImage) {
                    bottom.linkTo(image.bottom)
                    end.linkTo(image.end)
                },
            painter = painterResource(id = R.drawable.baseline_photo_camera_24),
            contentDescription = ""
        )
    }
}
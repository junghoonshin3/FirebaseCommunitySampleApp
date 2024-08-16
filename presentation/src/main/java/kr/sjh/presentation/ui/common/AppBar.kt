package kr.sjh.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.sjh.presentation.ui.chat.Profile
import kr.sjh.presentation.utill.clickableSingle

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    title: String,
    buttonTitle: String? = null,
    backIcon: ImageVector? = null,
    onBack: () -> Unit,
    onClick: (() -> Unit)? = null,
    profileImageUrl: String? = null
) {

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(45.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickableSingle(enabled = backIcon != null) {
                    onBack()
                }, contentAlignment = Alignment.Center
        ) {
            if (backIcon != null) {
                Image(
                    imageVector = backIcon,
                    colorFilter = ColorFilter.tint(Color.White),
                    contentDescription = "Back"
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            profileImageUrl?.let { url ->
                Profile(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape), imageUrl = url
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                textAlign = TextAlign.Center,
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(45.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable(enabled = buttonTitle != null) {
                    onClick?.invoke()
                }, contentAlignment = Alignment.Center
        ) {
            buttonTitle?.let {
                Text(
                    text = buttonTitle,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal
                )
            }

        }
    }
}
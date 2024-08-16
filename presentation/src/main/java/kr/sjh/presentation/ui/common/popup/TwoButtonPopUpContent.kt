package kr.sjh.presentation.ui.common.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot

@Composable
fun TwoButtonPopUpContent(
    title: String, subTitle: String, onConfirm: () -> Unit, onCancel: () -> Unit
) {
    val configuration = LocalConfiguration.current
    Box(
        modifier = Modifier
            .height((configuration.screenWidthDp / 2).dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .padding(15.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = subTitle,
                color = Color.White,
                fontSize = 16.sp
            )

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray)
                        .clickable {
                            onCancel()
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(text = "취소", color = Color.White, fontSize = 20.sp)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(carrot)
                        .clickable {
                            onConfirm()
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(text = "확인", color = Color.Red, fontSize = 20.sp)
                }

            }
        }
    }
}
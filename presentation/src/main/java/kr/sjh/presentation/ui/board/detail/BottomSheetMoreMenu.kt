package kr.sjh.presentation.ui.board.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomSheetMoreMenu(
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable {
                    onEdit()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "수정",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable {
                    onDelete()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "삭제",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Red
            )
        }
    }
}
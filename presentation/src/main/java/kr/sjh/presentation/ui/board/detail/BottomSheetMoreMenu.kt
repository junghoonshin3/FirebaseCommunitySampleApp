package kr.sjh.presentation.ui.board.detail

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Composable
fun BottomSheetMoreMenu(
    modifier: Modifier = Modifier,
    items: PersistentList<BottomSheetData>,
) {

    LazyColumn(
        modifier = modifier, verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(items) { index, item ->
            Log.d("sjh", item.toString())
            BottomSheetItem(
                onClick = item.onClick,
                title = item.title,
                fontSize = item.fontSize,
                color = item.color
            )
            if (items.size - 1 > index) {
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun BottomSheetItem(
    onClick: () -> Unit, title: String, fontSize: TextUnit, color: Color = Color.White
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable {
                onClick()
            }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = title, fontWeight = FontWeight.Bold, fontSize = fontSize, color = color
        )

    }
}

data class BottomSheetData(
    val title: String, val color: Color, val fontSize: TextUnit, val onClick: () -> Unit
)
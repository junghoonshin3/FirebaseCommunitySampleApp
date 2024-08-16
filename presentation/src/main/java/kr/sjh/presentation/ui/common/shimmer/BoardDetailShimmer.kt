package kr.sjh.presentation.ui.common.shimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kr.sjh.presentation.ui.theme.backgroundColor

@Composable
fun BoardDetailShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(10.dp)
    ) {
        ComponentRectangle()
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ComponentCircle()
            Spacer(modifier = Modifier.padding(4.dp))
            Column {
                Spacer(modifier = Modifier.padding(8.dp))
                ComponentRectangleLineLong()
                Spacer(modifier = Modifier.padding(4.dp))
                ComponentRectangleLineShort()
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        ComponentRectangleLineLong()
        Spacer(modifier = Modifier.height(10.dp))
        ComponentRectangleLineLong()

    }
}
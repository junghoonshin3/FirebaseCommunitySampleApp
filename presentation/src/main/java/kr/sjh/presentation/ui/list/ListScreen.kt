package kr.sjh.presentation.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ListScreen(navController: NavController, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(30) {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clickable {

                    }, title = it.toString()
            )
        }
    }
}

@Composable
fun ListItem(modifier: Modifier = Modifier, title: String) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title)
        }
    }
}



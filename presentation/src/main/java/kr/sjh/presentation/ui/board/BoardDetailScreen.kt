package kr.sjh.presentation.ui.board

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.navigation.NavController

@Composable
fun BoardDetailScreen(navController: NavController, modifier: Modifier = Modifier) {
    (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0f)
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Text("오호 안가려지는구만?")
        }
    }
}
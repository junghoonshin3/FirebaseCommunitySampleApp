package kr.sjh.pickup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import kr.sjh.pickup.ui.main.MainScreen
import kr.sjh.pickup.ui.theme.PickUpTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PickUpTheme {
                MainScreen(Modifier.fillMaxSize())
            }
        }
    }
}
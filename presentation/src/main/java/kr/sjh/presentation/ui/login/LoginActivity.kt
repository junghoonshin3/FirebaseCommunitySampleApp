package kr.sjh.presentation.ui.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import kr.sjh.presentation.navigation.LeafScreen
import kr.sjh.presentation.navigation.LoginNavGraph

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screenName = intent.extras?.getString("screenName") ?: LeafScreen.Login.route
        setContent {
            LoginNavGraph(
                startScreen = screenName,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
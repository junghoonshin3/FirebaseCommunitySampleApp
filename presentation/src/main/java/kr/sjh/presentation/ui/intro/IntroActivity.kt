package kr.sjh.presentation.ui.intro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.ClientErrorCause.TokenNotFound
import dagger.hilt.android.AndroidEntryPoint
import kr.sjh.domain.error.NotFoundUser
import kr.sjh.presentation.ui.login.LoginViewModel

@AndroidEntryPoint
class IntroActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        viewModel.hasToken(this, onResult = { userInfo, throwable ->
            if (userInfo != null) {
                startActivity(
                    Intent(Intent.ACTION_VIEW, "petory://main".toUri()).putExtra(
                        "userInfo", userInfo
                    )
                )
            } else if (throwable is NotFoundUser || throwable is ClientError) {
                startActivity(Intent(Intent.ACTION_VIEW, "petory://login".toUri()))
            } else if (throwable != null) {
                Toast.makeText(
                    this@IntroActivity,
                    "ERROR : ${throwable.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            finish()
        })
    }
}
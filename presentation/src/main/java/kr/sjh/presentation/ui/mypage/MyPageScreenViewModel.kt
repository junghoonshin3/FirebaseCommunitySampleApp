package kr.sjh.presentation.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.auth.firebase.AuthLogOutUseCase
import kr.sjh.domain.usecase.preferences.SavePreferenceUseCase
import javax.inject.Inject

@HiltViewModel
class MyPageScreenViewModel @Inject constructor(
    private val logOutUseCase: AuthLogOutUseCase,
    private val savePreferenceUseCase: SavePreferenceUseCase
) :
    ViewModel() {
    fun logOut() {
        viewModelScope.launch {
            logOutUseCase()
        }
    }
}
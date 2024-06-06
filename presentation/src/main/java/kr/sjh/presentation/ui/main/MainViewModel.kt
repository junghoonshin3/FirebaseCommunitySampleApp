package kr.sjh.presentation.ui.main

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.sjh.domain.model.UserInfo
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val readUserUseCase: ReadUserUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val userInfo = savedStateHandle.getStateFlow<UserInfo?>("userInfo", null)

    init {
        Log.d("MainViewModel", "${userInfo.value}")
    }
}
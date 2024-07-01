package kr.sjh.presentation.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.UserModel
import kr.sjh.domain.usecase.user.GetCurrentUserUseCase
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    val TAG = MainViewModel::class.java.simpleName

    val currentUser: StateFlow<UserModel?> =
        getCurrentUserUseCase()
            .filterIsInstance<ResultState.Success<UserModel>>()
            .map { it.data }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

}
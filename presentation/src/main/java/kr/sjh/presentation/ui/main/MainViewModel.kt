package kr.sjh.presentation.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.UserModel
import kr.sjh.domain.usecase.user.GetCurrentUserUseCase
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

        val currentUser: StateFlow<UserModel> =
        getCurrentUserUseCase()
            .filterIsInstance<ResultState.Success<UserModel>>()
            .map { it.data }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                UserModel()
            )
//    private val _currentUser: MutableStateFlow<UserModel> = MutableStateFlow(UserModel())
//    val currentUser = _currentUser.asStateFlow()
//
//    fun test() {
//        viewModelScope.launch {
//            getCurrentUserUseCase()
//                .collectLatest { result ->
//                    when (result) {
//                        is ResultState.Failure -> {}
//                        ResultState.Loading -> {}
//                        is ResultState.Success -> {
//                            result.data?.let {
//                                Log.d("sjh","${it.totalUnReadMessageCount}")
//                                _currentUser.value = it
//                            }
//
//                        }
//                    }
//                }
//        }
//
//    }
}
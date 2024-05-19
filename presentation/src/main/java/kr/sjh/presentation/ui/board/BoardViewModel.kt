package kr.sjh.presentation.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.board.ReadPostsUseCase
import kr.sjh.domain.usecase.board.UpdatePostUseCase
import kr.sjh.model.Post
import javax.inject.Inject

sealed interface BoardUiState {
    data class Success(val list: List<Post>) : BoardUiState
    data object Loading : BoardUiState

    data object Empty : BoardUiState

    data class Error(val throwable: Throwable) : BoardUiState
}

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val readPostsUseCase: ReadPostsUseCase
) : ViewModel() {

    val posts: StateFlow<BoardUiState> =
        readPostsUseCase().map<List<Post>, BoardUiState>(BoardUiState::Success)
            .onStart {
                emit(BoardUiState.Loading)
            }
            .catch {
                emit(BoardUiState.Error(it))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = BoardUiState.Loading
            )
}
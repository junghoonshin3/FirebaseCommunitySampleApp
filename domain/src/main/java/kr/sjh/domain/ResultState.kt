package kr.sjh.domain

sealed class ResultState<out R> {
    data class Success<out R>(val data: R) : ResultState<R>()
    data class Failure(val throwable: Throwable) : ResultState<Nothing>()
    data object Loading : ResultState<Nothing>()
}
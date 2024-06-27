package kr.sjh.domain.repository.firebase

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.UserModel

interface UserRepository {
    fun getCurrentUser(): Flow<ResultState<UserModel>>
    suspend fun getUser(uid: String): Flow<ResultState<UserModel>>
    suspend fun isUserExist(uid: String): Flow<ResultState<Boolean>>
    suspend fun signUp(userModel: UserModel): Flow<ResultState<Unit>>
}
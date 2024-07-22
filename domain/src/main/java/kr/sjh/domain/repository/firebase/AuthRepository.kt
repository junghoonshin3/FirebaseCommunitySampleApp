package kr.sjh.domain.repository.firebase

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.AuthUserModel
import kr.sjh.domain.model.CredentialModel

interface AuthRepository {
    suspend fun signIn(credential: CredentialModel): Flow<ResultState<Boolean>>
    suspend fun logOut()
    fun getCurrentAuthUser(): AuthUserModel?
}
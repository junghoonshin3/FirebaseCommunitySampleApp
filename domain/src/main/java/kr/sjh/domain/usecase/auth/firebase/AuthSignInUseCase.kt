package kr.sjh.domain.usecase.auth.firebase

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.CredentialModel

fun interface AuthSignInUseCase {
    suspend operator fun invoke(credential: CredentialModel): Flow<ResultState<Boolean>>
}
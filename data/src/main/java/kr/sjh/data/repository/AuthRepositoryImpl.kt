package kr.sjh.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kr.sjh.data.mapper.toAuthUserModel
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.AuthUserModel
import kr.sjh.domain.model.CredentialModel
import kr.sjh.domain.repository.firebase.AuthRepository
import kr.sjh.domain.repository.firebase.UserRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
) : AuthRepository {
    override suspend fun signIn(credential: CredentialModel) = flow {
        emit(ResultState.Loading)
        try {
            val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
            val result = auth.signInWithCredential(firebaseCredential).await()
            userRepository.isUserExist(result.user?.uid.toString())
                .collect {
                    emit(it)
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ResultState.Failure(e))
        }
    }

    override suspend fun logOut() {
        auth.signOut()
    }

    override fun getCurrentAuthUser(): AuthUserModel? {
        return auth.currentUser?.toAuthUserModel()
    }
}
package kr.sjh.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kr.sjh.data.mapper.toAuthUserModel
import kr.sjh.data.utils.Constants
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.AuthUserModel
import kr.sjh.domain.model.CredentialModel
import kr.sjh.domain.repository.firebase.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth, private val fireStore: FirebaseFirestore
) : AuthRepository {
    override suspend fun signIn(credential: CredentialModel) = callbackFlow {
        trySend(ResultState.Loading)
        try {
            val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
            val result = auth.signInWithCredential(firebaseCredential).await()
            result.user?.uid?.let { uid ->
                fireStore.collection(Constants.FirebaseCollectionUsers).document(uid).get()
                    .addOnSuccessListener {
                        trySend(ResultState.Success(it.exists()))
                    }.addOnFailureListener { e ->
                        trySend(ResultState.Failure(e))
                    }
            }
        } catch (e: Exception) {
            trySend(ResultState.Failure(e))
        }
        awaitClose {
            close()
        }
    }

    override suspend fun logOut() {
        auth.signOut()
    }

    override fun getCurrentAuthUser(): AuthUserModel? {
        return auth.currentUser?.toAuthUserModel()
    }
}
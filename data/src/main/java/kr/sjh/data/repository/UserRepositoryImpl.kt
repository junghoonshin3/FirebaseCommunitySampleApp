package kr.sjh.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kr.sjh.data.mapper.toUserEntity
import kr.sjh.data.mapper.toUserModel
import kr.sjh.data.model.UserEntity
import kr.sjh.data.utils.Constants
import kr.sjh.data.utils.FileUtil
import kr.sjh.domain.ResultState
import kr.sjh.domain.exception.FirebaseAuthCustomException
import kr.sjh.domain.exception.FirebaseAuthCustomException.UserNotFoundInUsers
import kr.sjh.domain.model.UserModel
import kr.sjh.domain.repository.firebase.UserRepository
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val fileUtil: FileUtil,
    private val context: Context
) : UserRepository {
    override fun getCurrentUser(): Flow<ResultState<UserModel>> = callbackFlow {
        val uid = auth.currentUser?.uid
        uid?.let {
            fireStore.collection(Constants.FirebaseCollectionUsers)
                .document(uid)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val userEntity = it.toObject(UserEntity::class.java)
                        if (userEntity != null) {
                            trySend(ResultState.Success(data = userEntity.toUserModel()))
                        } else {
                            trySend(ResultState.Failure(Exception("UserModel 객체만드는데 실패함")))
                        }
                    } else {
                        trySend(ResultState.Failure(Exception("시용자가 없는디요?")))
                    }
                }.addOnFailureListener { exception ->
                    trySend(ResultState.Failure(exception))
                }
        }
        awaitClose {
            close()
        }
    }


    override suspend fun getUser(uid: String): Flow<ResultState<UserModel>> = flow {
        emit(ResultState.Loading)
        try {
            val user = fireStore.collection(Constants.FirebaseCollectionUsers)
                .document(uid)
                .get()
                .await().toObject(UserModel::class.java) ?: return@flow emit(
                ResultState.Failure(
                    UserNotFoundInUsers()
                )
            )
            emit(ResultState.Success(user))
        } catch (e: Exception) {
            emit(ResultState.Failure(e))
        }
    }

    override suspend fun isUserExist(uid: String): Flow<ResultState<Boolean>> = flow {
        emit(ResultState.Loading)
        try {
            val isExist = fireStore.collection(Constants.FirebaseCollectionUsers)
                .document(uid)
                .get()
                .await().exists()
            emit(ResultState.Success(isExist))
        } catch (e: Exception) {
            emit(ResultState.Failure(e))
        }
    }

    override suspend fun signUp(userModel: UserModel) = flow {
        emit(ResultState.Loading)
        try {
            val uid = auth.currentUser?.uid
            uid?.let {
                Log.d("sjh", "before >>>>>>>>>>>>>>>> :${userModel.profileImageUrl}")
                val imageUrl = saveProfilePictureInStorage(uid, userModel.profileImageUrl!!)
                Log.d("sjh", "after >>>>>>>>>>>>>> :$imageUrl")
                fireStore.collection(Constants.FirebaseCollectionUsers)
                    .document(it)
                    .set(userModel.copy(profileImageUrl = imageUrl).toUserEntity())
                    .await()
                emit(ResultState.Success(Unit))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ResultState.Failure(e))
        }

    }

    private suspend fun saveProfilePictureInStorage(uid: String, profilePicture: String): String {
        return try {
            val imageName = UUID.randomUUID().toString()
            val imageUri = Uri.parse(profilePicture)
            val reSizedImageUri = fileUtil.run {
                if (!fileUtil.isLocalUri(imageUri)) {
                    val downloadImageUri =
                        fileUtil.downloadImageFromUrl(context, profilePicture)
                    resizeImage(context, uid, downloadImageUri, 100, 100)
                } else {
                    resizeImage(context, uid, imageUri, 100, 100)
                }
            }
            storage.reference.child("${Constants.FirebaseStorageProfileImages}/$uid/${imageName}_${reSizedImageUri.lastPathSegment}")
                .putFile(reSizedImageUri).await().storage.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
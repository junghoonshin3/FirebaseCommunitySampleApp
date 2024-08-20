package kr.sjh.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
import kr.sjh.data.utils.Constants.COL_USERS
import kr.sjh.data.utils.FileUtil
import kr.sjh.domain.ResultState
import kr.sjh.domain.exception.FirebaseAuthCustomException.UserNotFoundInUsers
import kr.sjh.domain.model.UserModel
import kr.sjh.domain.repository.firebase.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val fileUtil: FileUtil,
    private val context: Context
) : UserRepository {
    override fun getCurrentUser(): Flow<ResultState<UserModel>> = callbackFlow {
        val uid = auth.currentUser?.uid.toString()
        val listener =
            fireStore.collection(COL_USERS).document(uid).addSnapshotListener { snapshots, error ->
                if (error != null) {
                    trySend(ResultState.Failure(error))
                    return@addSnapshotListener
                }

                if (snapshots != null && snapshots.exists()) {
                    val userEntity = snapshots.toObject(UserEntity::class.java)
                    if (userEntity != null) {
                        trySend(ResultState.Success(data = userEntity.toUserModel()))
                    } else {
                        trySend(ResultState.Failure(Exception("UserEntity 생성 실패!")))
                    }
                }
            }
        awaitClose {
            listener.remove()
            close()
        }
    }


    override suspend fun getUser(uid: String): Flow<ResultState<UserModel>> = flow {
        emit(ResultState.Loading)
        try {
            val user = fireStore.collection(COL_USERS).document(uid).get().await()
                .toObject(UserModel::class.java) ?: return@flow emit(
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
            val isExist = fireStore.collection(COL_USERS).document(uid).get().await().exists()
            emit(ResultState.Success(isExist))
        } catch (e: Exception) {
            emit(ResultState.Failure(e))
        }
    }

    override suspend fun signUp(userModel: UserModel) = flow {
        emit(ResultState.Loading)
        try {
            val uid = auth.currentUser?.uid.toString()
            val imageUrl =
                saveProfilePictureInStorage("${uid}_profileImage.jpg", userModel.profileImageUrl)
            fireStore.collection(COL_USERS).document(uid)
                .set(userModel.copy(profileImageUrl = imageUrl).toUserEntity()).await()
            emit(ResultState.Success(Unit))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ResultState.Failure(e))
        }

    }

    private suspend fun saveProfilePictureInStorage(
        fileName: String, profilePicture: String
    ): String {
        return try {
            val imageUri = Uri.parse(profilePicture)
            val resizeBitmap = if (fileUtil.isLocalUri(imageUri)) {
                fileUtil.optimizedBitmap(imageUri, 100, 100)
            } else {
                val downLoadImage = fileUtil.downloadImageFromUrl(imageUri)
                fileUtil.optimizedBitmap(downLoadImage, 100, 100)
            }
            val resizeBitmapToFile = fileUtil.saveBitmapAsFile(resizeBitmap, fileName)
            resizeBitmap.recycle()
            storage.reference.child("${Constants.STORAGE_PROFILE_IMAGES}/${auth.uid.toString()}/${fileName}")
                .putFile(resizeBitmapToFile.toUri()).await().storage.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override fun banUser(banUid: String): Flow<ResultState<Unit>> = flow {
        try {

            fireStore.collection(COL_USERS).document(auth.currentUser?.uid.toString()).set(
                mapOf("banUsers" to FieldValue.arrayUnion(banUid)), SetOptions.merge()
            ).await()
            emit(ResultState.Success(Unit))
        } catch (e: Exception) {
            emit(ResultState.Failure(e))
        }

    }
}
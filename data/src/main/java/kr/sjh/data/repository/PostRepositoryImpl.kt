package kr.sjh.data.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.getField
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.sjh.data.mapper.toPostEntity
import kr.sjh.data.mapper.toPostModel
import kr.sjh.data.model.PostEntity
import kr.sjh.data.model.UserEntity
import kr.sjh.data.utils.Constants
import kr.sjh.data.utils.Constants.FirebaseCollectionPosts
import kr.sjh.data.utils.Constants.FirebaseCollectionUsers
import kr.sjh.data.utils.Constants.FirebaseStoragePostImages
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.model.UserModel
import kr.sjh.domain.repository.firebase.PostRepository
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : PostRepository {

    override fun getPosts(): Flow<ResultState<List<PostModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            val uid = auth.currentUser?.uid ?: return@callbackFlow
            val user = fireStore.collection(FirebaseCollectionUsers).document(uid).get().await()
                .toObject(UserEntity::class.java)
            val postDoc = fireStore.collection(FirebaseCollectionPosts)
            user?.let {
                val posts = postDoc.get().await().map {
                    it.toObject(PostEntity::class.java).toPostModel()
                }.filterNot { it.writerUid in user.banUsers }
                trySend(ResultState.Success(posts))
            }
        } catch (e: Exception) {
            trySend(ResultState.Failure(e))
            e.printStackTrace()
        }
        awaitClose {
            close()
        }
    }

    override fun getPost(postKey: String): Flow<ResultState<Pair<PostModel, UserModel>>> =
        callbackFlow {
            trySend(ResultState.Loading)
            fireStore.runTransaction { transaction ->
                val postDoc =
                    fireStore.collection(FirebaseCollectionPosts).document(postKey)
                val postSnapshot = transaction.get(postDoc)
                val postEntity = postSnapshot.toObject(PostEntity::class.java)
                    ?: throw RuntimeException("Post does not exist")

                // PostEntity를 PostModel로 변환
                val postModel = postEntity.toPostModel()
                // 작성자 정보 가져오기
                val userDoc =
                    fireStore.collection(FirebaseCollectionUsers).document(postModel.writerUid)
                val userSnapshot = transaction.get(userDoc)
                val userModel = userSnapshot.toObject(UserModel::class.java)
                    ?: throw RuntimeException("User does not exist")
                postModel to userModel
            }.addOnSuccessListener {
                // 트랜잭션 성공
                trySend(ResultState.Success(it))
            }.addOnFailureListener { exception ->
                // 트랜잭션 실패
                trySend(ResultState.Failure(exception))
            }
            awaitClose {
                close()
            }
        }

    override suspend fun addPost(postModel: PostModel): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            val uid = auth.currentUser?.uid ?: return@callbackFlow

            val userEntity =
                fireStore.collection(FirebaseCollectionUsers).document(uid).get().await()
                    .toObject(UserEntity::class.java) ?: return@callbackFlow

            // 이미지 업로드 후 storage에서 다운로드 url 획득
            val downloadUrls = if (postModel.images.isNotEmpty()) {
                uploadImagesAndGetUrls(uid, postModel.postKey, postModel.images)
            } else {
                emptyList() // 이미지가 없으면 빈 리스트 반환
            }

            // 다운로드 URL을 PostModel에 설정
            val postEntity = postModel.copy(
                nickName = userEntity.nickName.toString(), writerUid = uid, images = downloadUrls
            ).toPostEntity()

            val postRef =
                fireStore.collection(FirebaseCollectionPosts).document(postEntity.postKey)
            val userRef = fireStore.collection(FirebaseCollectionUsers).document(uid)

            fireStore.runBatch { batch ->
                batch.set(postRef, postEntity)
                batch.update(userRef, "myPosts", FieldValue.arrayUnion(postEntity.postKey))
            }.addOnSuccessListener {
                trySend(ResultState.Success(postEntity.postKey))
            }.addOnFailureListener { exception ->
                trySend(ResultState.Failure(exception))
            }

        } catch (exception: Exception) {
            trySend(ResultState.Failure(exception))
        }
        awaitClose {
            close()
        }
    }

    override suspend fun removePost(postKey: String): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            val uid = auth.currentUser?.uid ?: return@callbackFlow
            val storageRef =
                storage.reference.child(FirebaseStoragePostImages).child(uid).child(postKey)
            val items = storageRef.listAll().await().items
            items.map {
                it.delete().await()
            }
            val postRef = fireStore.collection(FirebaseCollectionPosts).document(postKey)
            val userRef = fireStore.collection(FirebaseCollectionUsers).document(uid)
            val myPosts = (userRef.get().await().data?.get("myPosts") as List<*>).toMutableList()
            fireStore.runBatch {
                it.delete(postRef)
                if (myPosts.isNotEmpty() && myPosts.remove(postKey)) {
                    it.update(userRef, "myPosts", myPosts.toList())
                }
            }.addOnSuccessListener {
                trySend(ResultState.Success(Unit))
            }.addOnFailureListener {
                trySend(ResultState.Failure(it))
            }
        } catch (e: Exception) {
            trySend(ResultState.Failure(e))
        }

        awaitClose {
            close()
        }
    }

    override suspend fun updatePost(postModel: PostModel): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            val storageRef =
                storage.reference.child(FirebaseStoragePostImages).child(postModel.writerUid)
                    .child(postModel.postKey)
            val items = storageRef.listAll().await().items

            val existingImages = items.map { it.downloadUrl.await().toString() }

            // 삭제할 이미지 필터링 및 삭제
            items.filter { it.downloadUrl.await().toString() !in postModel.images }.map {
                it.delete().await()
            }

            // 새롭게 추가된 이미지 필터링 및 업로드
            postModel.images.filter { it !in existingImages }.map {
                val uri = it.toUri()
                val newImageRef = storageRef.child(uri.lastPathSegment ?: "new_image.jpg")
                newImageRef.putFile(uri).await()
            }

            val postRef =
                fireStore.collection(FirebaseCollectionPosts).document(postModel.postKey)

            postRef.update(
                mapOf("title" to postModel.title,
                    "content" to postModel.content,
                    "images" to storageRef.listAll().await().items.map {
                        it.downloadUrl.await().toString()
                    })
            ).addOnSuccessListener {
                trySend(ResultState.Success(Unit))
            }.addOnFailureListener {
                trySend(ResultState.Failure(it))
            }
        } catch (e: Exception) {
            trySend(ResultState.Failure(e))
        }
        awaitClose {
            close()
        }
    }

    override fun updateReadCount(postKey: String): Flow<ResultState<Unit>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            val postRef = fireStore.collection(FirebaseCollectionPosts).document(postKey)
            fireStore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val newReadCount = snapshot.getLong("readCount")?.plus(1)
                transaction.update(
                    postRef, "readCount", newReadCount
                )
            }.addOnSuccessListener {
                trySend(ResultState.Success(Unit))
            }.addOnFailureListener {
                trySend(ResultState.Failure(it))
            }

        } catch (e: Exception) {
            trySend(ResultState.Failure(e))
        }

        awaitClose {
            close()
        }
    }


    private suspend fun uploadImagesAndGetUrls(
        uid: String, postKey: String, imageUris: List<String>
    ): List<String> = withContext(Dispatchers.IO) {
        imageUris.map {
            val uri = Uri.parse(it)
            async {
                storage.reference.child("$FirebaseStoragePostImages/$uid/$postKey/${uri.lastPathSegment}")
                    .putFile(uri).await().storage.downloadUrl.await().toString()
            }
        }.awaitAll()
    }

}
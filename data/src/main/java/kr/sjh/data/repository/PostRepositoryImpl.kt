package kr.sjh.data.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.sjh.data.mapper.toPostEntity
import kr.sjh.data.mapper.toPostModel
import kr.sjh.data.model.PostEntity
import kr.sjh.data.model.UserEntity
import kr.sjh.data.utils.Constants.COL_POSTS
import kr.sjh.data.utils.Constants.COL_USERS
import kr.sjh.data.utils.Constants.STORAGE_POST_IMAGES
import kr.sjh.data.utils.FileUtil
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.model.UserModel
import kr.sjh.domain.repository.firebase.PostRepository
import java.util.UUID
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val fileUtil: FileUtil
) : PostRepository {

    override fun getPosts(size: Long, lastTime: Long?): Flow<ResultState<List<PostModel>>> =
        callbackFlow {
            trySend(ResultState.Loading)
            try {
                val uid = auth.currentUser?.uid.toString()
                val user = fireStore.collection(COL_USERS).document(uid).get().await()
                    .toObject(UserEntity::class.java)
                Log.d("banUsers", "${user?.banUsers}")

                val query = if (lastTime == null) {
                    fireStore.collection(COL_POSTS).limit(size)
                        .orderBy("timeStamp", Query.Direction.DESCENDING)

                } else {
                    val lastTimestamp =
                        Timestamp(lastTime / 1000, (lastTime % 1000 * 1000000).toInt())
                    fireStore.collection(COL_POSTS).limit(size)
                        .whereLessThan("timeStamp", lastTimestamp)
                        .orderBy("timeStamp", Query.Direction.DESCENDING)
                }

                query.get().addOnSuccessListener {
                    Log.d("getPosts", "${it.documents.size}")
                    val posts = it.documentChanges.mapNotNull { documentChange ->
                        documentChange.document.toObject(PostEntity::class.java).toPostModel()
                    }.filterNot { postModel ->
                        user?.banUsers?.contains(postModel.writerUid) ?: false
                    }
                    trySend(ResultState.Success(posts))
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    trySend(ResultState.Failure(e))
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
                val postDoc = fireStore.collection(COL_POSTS).document(postKey)
                val postSnapshot = transaction.get(postDoc)
                val postEntity = postSnapshot.toObject(PostEntity::class.java)
                    ?: throw RuntimeException("Post does not exist")

                // PostEntity를 PostModel로 변환
                val postModel = postEntity.toPostModel()
                // 작성자 정보 가져오기
                val userDoc = fireStore.collection(COL_USERS).document(postModel.writerUid)
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
            val uid = auth.currentUser?.uid.toString()

            val userEntity = fireStore.collection(COL_USERS).document(uid).get().await()
                .toObject(UserEntity::class.java)

            val postRef = fireStore.collection(COL_POSTS).document()

            val postKey = postRef.id

            val userRef = fireStore.collection(COL_USERS).document(uid)

            // 이미지 업로드 후 storage에서 다운로드 url 획득
            val downloadUrls = if (postModel.images.isNotEmpty()) {
                uploadImagesAndGetUrls(postKey, postModel.images)
            } else {
                emptyList() // 이미지가 없으면 빈 리스트 반환
            }

            // 다운로드 URL을 PostModel에 설정
            val postEntity = postModel.copy(
                postKey = postKey,
                nickName = userEntity?.nickName.toString(),
                writerUid = uid,
                images = downloadUrls
            ).toPostEntity()



            fireStore.runBatch { batch ->
                batch.set(postRef, postEntity)
                batch.update(userRef, "myPosts", FieldValue.arrayUnion(postKey))
            }.addOnSuccessListener {
                trySend(ResultState.Success(postKey))
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
            val uid = auth.currentUser?.uid.toString()
            val storageRef = storage.reference.child(STORAGE_POST_IMAGES).child(uid).child(postKey)
            val items = storageRef.listAll().await().items
            items.map {
                it.delete().await()
            }
            val postRef = fireStore.collection(COL_POSTS).document(postKey)
            val userRef = fireStore.collection(COL_USERS).document(uid)
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
            val storageRef = storage.reference.child(STORAGE_POST_IMAGES).child(postModel.writerUid)
                .child(postModel.postKey)

            val existingImages = storageRef.listAll().await().items

            val imagesToDelete = mutableListOf<StorageReference>()

            existingImages.forEach {
                if (it.downloadUrl.await().toString() !in postModel.images) {
                    imagesToDelete.add(it)
                }
            }

            imagesToDelete.map { it.delete().await() }

            existingImages.removeAll(imagesToDelete)

            val newExistingImages = existingImages.map { it.downloadUrl.await().toString() }

            val newImages = postModel.images.filter { it !in newExistingImages }

            val uploadImageUrl = uploadImagesAndGetUrls(postModel.postKey, newImages)

            val postRef = fireStore.collection(COL_POSTS).document(postModel.postKey)

            postRef.update(
                mapOf(
                    "title" to postModel.title,
                    "content" to postModel.content,
                    "images" to newExistingImages + uploadImageUrl
                )
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
            val postRef = fireStore.collection(COL_POSTS).document(postKey)
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
        postKey: String, imageUris: List<String>
    ): List<String> = withContext(Dispatchers.IO) {
        imageUris.mapIndexed { index, s ->
            val imageUri = Uri.parse(s)
            val fileName = UUID.randomUUID().toString()
            async {
                val resizeBitmap = fileUtil.optimizedBitmap(imageUri, 400, 400)
                val resizeBitmapToFile = fileUtil.saveBitmapAsFile(resizeBitmap, "$fileName")
                resizeBitmap.recycle()
                storage.reference.child("$STORAGE_POST_IMAGES/${auth.uid.toString()}/$postKey/$fileName")
                    .putFile(resizeBitmapToFile.toUri()).await().storage.downloadUrl.await()
                    .toString()

            }
        }.awaitAll()
    }
}
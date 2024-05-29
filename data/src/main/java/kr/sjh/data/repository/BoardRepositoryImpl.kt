package kr.sjh.data.repository

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.sjh.domain.model.Post
import kr.sjh.domain.repository.BoardRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BoardRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val db: FirebaseDatabase
) : BoardRepository {

    private val ref = db.reference.child("posts")
    override fun readPosts(): Flow<List<Post>> = ref.snapshots.map {
        if (it.childrenCount > 0) {
            it.children.mapNotNull {
                it.getValue(Post::class.java)
            }.sortedByDescending {
                it.createdAt
            }
        } else {
            emptyList()
        }
    }


    override fun readPost(postKey: String): Flow<Post> =
        ref.snapshots.map { snapshot ->
            snapshot.child(postKey).getValue(Post::class.java)
        }.filterNotNull()

    override suspend fun createPost(post: Post) = runCatching {
        suspendCoroutine { continuation ->
            ref.child(post.key).setValue(post)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun deletePost(postKey: String): Result<Unit> = runCatching {
        suspendCoroutine { continuation ->
            ref.child(postKey).removeValue()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }.recoverCatching {
        throw it
    }

    override suspend fun updatePost(post: Post): Result<Unit> =
        runCatching {
            suspendCoroutine { continuation ->
                ref.child(post.key).updateChildren(
                    post.toMap()
                ).addOnSuccessListener { continuation.resume(Unit) }
                    .addOnFailureListener { continuation.resumeWithException(it) }
            }
        }


    override suspend fun uploadImages(postKey: String, images: List<Uri>) =
        withContext(Dispatchers.IO) {
            runCatching {
                images.map { image ->
                    async {
                        storage
                            .reference
                            .child("images/$postKey/${image.lastPathSegment}")
                            .putFile(image)
                            .await()
                            .storage
                            .downloadUrl
                            .await()
                    }
                }.awaitAll()
            }
        }


    override suspend fun uploadImage(userId: String, image: Uri): Result<Uri> =
        withContext(Dispatchers.IO) {
            runCatching {
                storage.reference.child("images/$userId/${image.lastPathSegment}").putFile(image)
                    .await()
                    .storage.downloadUrl.await()
            }
        }

    override suspend fun removeImages(postKey: String): Result<List<Void>> =
        withContext(Dispatchers.IO) {
            runCatching {
                storage.reference.child("images/$postKey").listAll()
                    .await()
                    .items
                    .map { image ->
                        async {
                            image.delete().await()
                        }
                    }.awaitAll()
            }
        }


    override fun createPostKey(): String = ref.push().key.toString()
}
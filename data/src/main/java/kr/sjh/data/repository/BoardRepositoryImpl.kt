package kr.sjh.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
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

    override suspend fun createPost(post: Post): Result<Unit> = runCatching {
        suspendCoroutine { continuation ->
            val key = ref.push().key.toString()
            ref.child(key).setValue(post.copy(key = key))
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
}
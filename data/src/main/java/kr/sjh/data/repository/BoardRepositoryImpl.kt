package kr.sjh.data.repository

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kr.sjh.domain.repository.BoardRepository
import kr.sjh.domain.usecase.login.model.Post
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BoardRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val db: FirebaseDatabase
) : BoardRepository {

    private val ref = db.reference.child("posts")
    override suspend fun readPosts(): Flow<List<Post>> = ref.snapshots.map {
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

    override suspend fun updatePost(post: Map<String, Any>): Result<Boolean> =
        runCatching {
            _updatePost(post)
        }.recoverCatching {
            throw it
        }


    private suspend fun _updatePost(post: Map<String, Any>) = suspendCoroutine { continuation ->
        ref.updateChildren(
            post
        ).addOnSuccessListener { continuation.resume(true) }
            .addOnFailureListener { continuation.resumeWithException(it) }
    }
}
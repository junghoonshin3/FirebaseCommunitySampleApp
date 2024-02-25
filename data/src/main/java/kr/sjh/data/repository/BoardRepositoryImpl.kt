package kr.sjh.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kr.sjh.domain.repository.BoardRepository
import kr.sjh.domain.usecase.login.model.Post
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BoardRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val db: FirebaseDatabase
) :
    BoardRepository {

    private val ref = db.reference.child("posts")
    override fun readPosts(): Flow<List<Post>> =
        ref.snapshots.map { snapshot ->
            snapshot.children.mapNotNull { post ->
                post.getValue(Post::class.java)
            }
        }

    override suspend fun createPost(post: Post): Result<Boolean> =
        runCatching {
            insertPost(post)
        }.recoverCatching {
            throw it
        }

    private suspend fun insertPost(post: Post) = suspendCoroutine { continuation ->
        val key = ref.push().key.toString()
        ref.child(key).setValue(post.copy(key = key))
            .addOnSuccessListener {
                continuation.resume(true)
            }
            .addOnFailureListener {
                continuation.resumeWithException(it)
            }
    }

}
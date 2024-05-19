package kr.sjh.data.repository

import kotlinx.coroutines.flow.Flow
import kr.sjh.model.Post

interface BoardRepository {
    fun readPosts(): Flow<List<Post>>

    fun readPost(postKey: String): Flow<Post>

    suspend fun createPost(post: Post): Result<Unit>
    suspend fun deletePost(post: Post): Result<Unit>
    suspend fun updatePost(post: Post): Result<Unit>
}
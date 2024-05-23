package kr.sjh.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.model.Post

interface BoardRepository {
    fun readPosts(): Flow<List<Post>>

    fun readPost(postKey: String): Flow<Post>

    suspend fun createPost(post: Post): Result<Unit>
    suspend fun deletePost(postKey: String): Result<Unit>
    suspend fun updatePost(post: Post): Result<Unit>
}
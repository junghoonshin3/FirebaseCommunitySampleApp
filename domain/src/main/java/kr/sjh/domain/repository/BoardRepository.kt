package kr.sjh.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.usecase.login.model.Post

interface BoardRepository {
    fun readPosts(): Flow<List<Post>>

    fun readPost(postKey: String): Flow<Post>

    suspend fun createPost(post: Post): Result<Unit>

    //    suspend fun deletePost(post: Post): Flow<Result<Post>>
    suspend fun updatePost(post: Map<String, Any>): Result<Boolean>
}
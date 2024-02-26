package kr.sjh.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.usecase.login.model.Post

interface BoardRepository {
    fun readPosts(): Flow<List<Post>>

    suspend fun createPost(post: Post): Result<Boolean>

//    suspend fun deletePost(post: Post): Flow<Result<Post>>
//    suspend fun updatePost():Flow<Result<Post>>
}
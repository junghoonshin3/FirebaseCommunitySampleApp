package kr.sjh.domain.repository.firebase

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.model.UserModel

interface PostRepository {
    fun getPosts(): Flow<ResultState<List<PostModel>>>
    fun getPost(postKey: String): Flow<ResultState<Pair<PostModel, UserModel>>>
    suspend fun addPost(postModel: PostModel): Flow<ResultState<String>>
    suspend fun removePost(postKey: String): Flow<ResultState<Unit>>
    suspend fun updatePost(postModel: PostModel): Flow<ResultState<Unit>>
    fun updateReadCount(postKey: String): Flow<ResultState<Unit>>

}
package kr.sjh.domain.repository

import kr.sjh.domain.model.UserInfo

interface LoginRepository {
    suspend fun createUser(user: UserInfo): Result<UserInfo>
    suspend fun readUser(id: String): Result<UserInfo>
    suspend fun deleteUser(id: String?): Result<Boolean>

    suspend fun updateUser(user: UserInfo): Result<Boolean>

}
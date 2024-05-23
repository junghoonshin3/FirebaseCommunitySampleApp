package kr.sjh.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kr.sjh.domain.error.NotFoundUser
import kr.sjh.domain.model.UserInfo
import kr.sjh.domain.repository.LoginRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginRepositoryImpl @Inject constructor(
    private val db: FirebaseDatabase,
) : LoginRepository {


    override suspend fun createUser(user: UserInfo) = runCatching {
        create(user)
    }.recoverCatching {
        throw it
    }

    override suspend fun readUser(id: String): Result<UserInfo> =
        runCatching {
            suspendCoroutine { continuation ->
                db.reference.child("users").child(id)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userInfo = snapshot.getValue(UserInfo::class.java)
                            if (userInfo != null) {
                                continuation.resume(userInfo)
                            } else {
                                continuation.resumeWithException(NotFoundUser())
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            continuation.resumeWithException(error.toException())
                        }

                    })
            }
        }


    override suspend fun deleteUser(id: String?) = runCatching {
        delete(id)
    }.recoverCatching {
        throw it
    }

    override suspend fun updateUser(user: UserInfo): Result<Boolean> = runCatching {
        suspendCoroutine { continuation ->
            db.reference.child("users").child(user.id!!).setValue(user).addOnSuccessListener {
                continuation.resume(true)
            }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }


    private suspend fun create(
        user: UserInfo
    ) = suspendCoroutine { continuation ->
        db.reference.child("users").child(user.id.toString()).setValue(
            user
        ).addOnSuccessListener {
            continuation.resume(user)
        }.addOnFailureListener {
            continuation.resumeWithException(it)
        }

    }

    private suspend fun delete(
        id: String?,
    ) = suspendCoroutine { continuation ->
        if (!id.isNullOrBlank()) {
            db.reference.child("users").child(id).removeValue { error, ref ->
                if (error != null) {
                    continuation.resumeWithException(error.toException())
                } else {
                    continuation.resume(true)
                }
            }
        } else {
            continuation.resumeWithException(RuntimeException("ID is NullOrBlank"))
        }
    }
}
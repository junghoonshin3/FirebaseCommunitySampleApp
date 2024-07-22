package kr.sjh.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kr.sjh.data.mapper.toChatMessageEntity
import kr.sjh.data.mapper.toChatMessageModel
import kr.sjh.data.model.ChatMessageEntity
import kr.sjh.data.model.UserEntity
import kr.sjh.data.utils.Constants
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatMessageModel
import kr.sjh.domain.model.ChatRoomModel
import kr.sjh.domain.repository.firebase.ChatRepository
import kr.sjh.domain.util.generateUniqueChatKey
import java.util.Date
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firebase: FirebaseFirestore, private val auth: FirebaseAuth
) : ChatRepository {

    override fun getInitialMessages(
        roomId: String, size: Long
    ): Flow<ResultState<ChatMessageModel>> = callbackFlow {
        auth.currentUser?.uid?.let {
            val listener = firebase.collection(Constants.FirebaseCollectionChats).document(roomId)
                .collection(Constants.FirebaseCollectionChatMessages)
                .orderBy("timeStamp", Query.Direction.ASCENDING).limitToLast(size)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        error.printStackTrace()
                        trySend(ResultState.Failure(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {
                            Log.d(
                                "sjh", "snapshot.documentChanges : ${snapshot.documentChanges.size}"
                            )
                            snapshot.documentChanges.map { dc ->
                                when (dc.type) {
                                    DocumentChange.Type.ADDED -> {
                                        val message =
                                            dc.document.toObject(ChatMessageEntity::class.java)
                                                .toChatMessageModel()
                                        trySend(ResultState.Success(message))
                                    }

                                    DocumentChange.Type.MODIFIED -> {

                                    }

                                    DocumentChange.Type.REMOVED -> {

                                    }
                                }
                            }
                        }
                    }
                }

            awaitClose {
                listener.remove()
                close()
            }
        }
    }

    override fun getNextMessages(
        roomId: String, size: Long, fromTime: Long
    ): Flow<ResultState<List<ChatMessageModel>>> = callbackFlow {

        // fromTime을 Timestamp 객체로 변환
        val timestamp = Timestamp(fromTime / 1000, (fromTime % 1000 * 1000000).toInt())
        Log.d("sjh", "${timestamp.toDate()}")
        firebase.collection(Constants.FirebaseCollectionChats).document(roomId)
            .collection(Constants.FirebaseCollectionChatMessages)
            .whereLessThan("timeStamp", timestamp).orderBy("timeStamp", Query.Direction.DESCENDING)
            .limit(size).get().addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val messages = snapshot.toObjects(ChatMessageEntity::class.java).map {
                        it.toChatMessageModel()
                    }
                    trySend(ResultState.Success(messages))
                } else {
                    trySend(ResultState.Success(emptyList()))
                }
            }.addOnFailureListener { exception ->
                trySend(ResultState.Failure(exception))
            }

        awaitClose {
            close()
        }
    }

    override suspend fun sendMessage(
        message: ChatMessageModel
    ): Flow<ResultState<Unit>> = flow {
        try {
            auth.currentUser?.uid?.let { uid ->
                val roomId = generateUniqueChatKey(uid, message.receiverUid)
                val chatsDoc =
                    firebase.collection(Constants.FirebaseCollectionChats).document(roomId)
                val messagesDoc =
                    firebase.collection(Constants.FirebaseCollectionChats).document(roomId)
                        .collection(Constants.FirebaseCollectionChatMessages).document()

                val isRoomExist = chatsDoc.get().await().exists()
                if (!isRoomExist) {
                    createChatRoom(roomId, uid, message.receiverUid)
                }
                val msgEntity = message.copy(messageId = messagesDoc.id).toChatMessageEntity()
                messagesDoc.set(msgEntity).await()
                val recentMessage =
                    messagesDoc.get().await().toObject(ChatMessageEntity::class.java)
                updateRecentMessage(roomId, recentMessage?.message!!, recentMessage.timeStamp!!)
                emit(ResultState.Success(Unit))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ResultState.Failure(e))
        }
    }

    override fun getChatRooms(): Flow<ResultState<List<ChatRoomModel>>> = callbackFlow {
        auth.currentUser?.uid?.let { uid ->
            try {
                val listener = firebase.collection(Constants.FirebaseCollectionChats)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(ResultState.Failure(error))
                        }
                        if (snapshot != null && !snapshot.isEmpty) {
                            val chatRooms = snapshot.documentChanges.filter { dc ->
                                Log.d("sjh", "dc.document.id : ${dc.document.id}")
                                Log.d("sjh", "filter  >>> ${dc.document.id.contains(uid)}")
                                dc.document.id.contains(uid)
                            }.map { dc ->
                                val inviterMap = dc.document.data["inviter"] as? Map<*, *>
                                val inviteeMap = dc.document.data["invitee"] as? Map<*, *>
                                ChatRoomModel(
                                    roomId = dc.document.id,
                                    recentMessage = dc.document.data["recentMessage"] as String,
                                    timeStamp = (dc.document.data["timeStamp"] as Timestamp).toDate(),
                                    inviter = ChatRoomModel.Inviter(
                                        uid = inviterMap!!["uid"] as String,
                                        nickName = inviterMap["nickName"] as String,
                                        profileImageUrl = inviterMap["profileImageUrl"] as String
                                    ),
                                    invitee = ChatRoomModel.Invitee(
                                        uid = inviteeMap!!["uid"] as String,
                                        nickName = inviteeMap["nickName"] as String,
                                        profileImageUrl = inviteeMap["profileImageUrl"] as String
                                    )
                                )
                            }

                            Log.d("sjh", "chatRooms : ${chatRooms.size}")
                            trySend(ResultState.Success(chatRooms))
                        }
                    }
                awaitClose {
                    listener.remove()
                    close()
                }
            } catch (e: Exception) {
                trySend(ResultState.Failure(e))
            }
        }
    }

    private suspend fun createChatRoom(
        roomId: String, inviterUid: String, inviteeUid: String
    ) {
        val inviterDoc = firebase.collection(Constants.FirebaseCollectionUsers).document(inviterUid)
        val inviteeDoc = firebase.collection(Constants.FirebaseCollectionUsers).document(inviteeUid)
        val inviter = inviterDoc.get().await().toObject(UserEntity::class.java)
        val invitee = inviteeDoc.get().await().toObject(UserEntity::class.java)
        firebase.collection(Constants.FirebaseCollectionChats).document(roomId).set(
            mapOf(
                "inviter" to mapOf(
                    "uid" to inviterUid,
                    "profileImageUrl" to inviter?.profileImageUrl,
                    "nickName" to inviter?.nickName
                ), "invitee" to mapOf(
                    "uid" to inviteeUid,
                    "profileImageUrl" to invitee?.profileImageUrl,
                    "nickName" to invitee?.nickName
                ), "recentMessage" to "", "timeStamp" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    private suspend fun updateRecentMessage(
        roomId: String, message: String, timeStamp: Date
    ) {
        firebase.collection(Constants.FirebaseCollectionChats).document(roomId).update(
            mapOf(
                "recentMessage" to message, "timeStamp" to Timestamp(timeStamp)
            )
        ).await()
    }

}
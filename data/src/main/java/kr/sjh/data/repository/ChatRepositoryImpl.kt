package kr.sjh.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import kr.sjh.data.mapper.toChatMessageModel
import kr.sjh.data.mapper.toChatRoomModel
import kr.sjh.data.model.ChatMessageEntity
import kr.sjh.data.model.ChatRoomEntity
import kr.sjh.data.model.ChatRoomUserEntity
import kr.sjh.data.model.UserEntity
import kr.sjh.data.utils.Constants.COL_CHAT_MESSAGES
import kr.sjh.data.utils.Constants.COL_CHAT_ROOMS
import kr.sjh.data.utils.Constants.COL_MY_CHAT_ROOMS
import kr.sjh.data.utils.Constants.COL_TOTAL_UNREAD_MESSAGE_COUNT
import kr.sjh.data.utils.Constants.COL_USERS
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatMessageModel
import kr.sjh.domain.model.ChatRoomModel
import kr.sjh.domain.repository.firebase.ChatRepository
import kr.sjh.domain.util.generateUniqueChatKey
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firebase: FirebaseFirestore, private val auth: FirebaseAuth
) : ChatRepository {

    override fun getInitialMessages(
        roomId: String, size: Long
    ): Flow<ResultState<List<ChatMessageModel>>> = callbackFlow {
        Log.d("getInitialMessages", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        var listener: ListenerRegistration? =
            firebase.collection(COL_CHAT_MESSAGES).document(roomId).collection("contents")
                .orderBy("timeStamp", Query.Direction.DESCENDING).limit(size)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        trySend(ResultState.Failure(error))
                        return@addSnapshotListener
                    }
                    if (snapshots != null && !snapshots.isEmpty) {
                        val messages =
                            snapshots.toObjects(ChatMessageEntity::class.java).map { entity ->
                                entity.toChatMessageModel()
                            }
                        trySend(ResultState.Success(messages))
                    }
                }
        awaitClose {
            Log.d("getInitialMessages", "getInitialMessages remove")
            listener?.remove()
            listener = null
            close()
        }
    }

    override fun getNextMessages(
        roomId: String, size: Long, fromTime: Long
    ): Flow<ResultState<List<ChatMessageModel>>> = callbackFlow {
        // fromTime을 Timestamp 객체로 변환
        val timestamp = Timestamp(fromTime / 1000, (fromTime % 1000 * 1000000).toInt())
        Log.d("sjh", "${timestamp.toDate()}")
        firebase.collection(COL_CHAT_MESSAGES).document(roomId).collection("contents")
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
    ): Flow<ResultState<Unit>> = flow<ResultState<Unit>> {
        emit(ResultState.Loading)

        val roomId = generateUniqueChatKey(message.senderUid, message.receiverUid)

        val meChatRoomDoc = firebase.collection(COL_CHAT_ROOMS).document(message.senderUid)
            .collection(COL_MY_CHAT_ROOMS).document(roomId)

        val youChatRoomDoc = firebase.collection(COL_CHAT_ROOMS).document(message.receiverUid)
            .collection(COL_MY_CHAT_ROOMS).document(roomId)

        val youTotalUnReadMessageCountDoc =
            firebase.collection(COL_TOTAL_UNREAD_MESSAGE_COUNT).document(message.receiverUid)

        val messageDoc =
            firebase.collection(COL_CHAT_MESSAGES).document(roomId).collection("contents")
                .document()

        val meChatRoomExist = meChatRoomDoc.get().await().exists()

        val youChatRoomExist = youChatRoomDoc.get().await().exists()

        if (!meChatRoomExist || !youChatRoomExist) {
            createChat(
                roomId, message.senderUid, message.receiverUid, meChatRoomDoc, youChatRoomDoc
            )
        }

        val nowTime = FieldValue.serverTimestamp()

        firebase.runTransaction { transaction ->
            transaction.set(
                messageDoc, mapOf(
                    "messageId" to messageDoc.id,
                    "senderUid" to message.senderUid,
                    "receiverUid" to message.receiverUid,
                    "message" to message.text,
                    "timeStamp" to nowTime
                ), SetOptions.merge()
            )

            transaction.set(
                meChatRoomDoc, mapOf(
                    "recentMessageTimeStamp" to nowTime, "recentMessage" to message.text
                ), SetOptions.merge()
            )
        }.await()

        firebase.runTransaction { transaction ->

            val yourLastVisitedTimestamp =
                transaction.get(youChatRoomDoc).getTimestamp("lastVisitedTimeStamp") as Timestamp

            val recentMessageTimestamp =
                transaction.get(meChatRoomDoc).getTimestamp("recentMessageTimeStamp")

            val youChatRoomUpdates = mutableMapOf(
                "recentMessageTimeStamp" to nowTime, "recentMessage" to message.text
            )

            if (yourLastVisitedTimestamp.toDate() < recentMessageTimestamp?.toDate()) {
                youChatRoomUpdates["unReadMessageCount"] = FieldValue.increment(1)
                transaction.set(
                    youTotalUnReadMessageCountDoc, mapOf(
                        "totalUnReadMessageCount" to FieldValue.increment(1)
                    ), SetOptions.merge()
                )
            }
            transaction.set(youChatRoomDoc, youChatRoomUpdates, SetOptions.merge())
        }.await()
    }.catch { error ->
        ResultState.Failure(error)
    }


    override fun getChatRooms(): Flow<ResultState<List<ChatRoomModel>>> {
        val uid = auth.currentUser?.uid.toString()
        val messageCol =
            firebase.collection(COL_CHAT_ROOMS).document(uid).collection(COL_MY_CHAT_ROOMS)
        return messageCol.snapshots().map { snapshot ->
            if (snapshot.documents.isEmpty()) {
                ResultState.Success(emptyList())
            } else {
                val chatRooms = snapshot.documents.mapNotNull { dc ->
                    dc.toObject(ChatRoomEntity::class.java)?.toChatRoomModel()
                }
                ResultState.Success(chatRooms)
            }
        }.catch { error ->
            ResultState.Failure(error)
        }
    }


    private suspend fun createChat(
        roomId: String,
        meUid: String,
        youUid: String,
        meChatRoom: DocumentReference,
        youChatRoom: DocumentReference
    ) {

        val myInfo = firebase.collection(COL_USERS).document(meUid)
        val youInfo = firebase.collection(COL_USERS).document(youUid)
        val myMessageCountCol = firebase.collection(COL_TOTAL_UNREAD_MESSAGE_COUNT).document(meUid)
        val yourMessageCountCol =
            firebase.collection(COL_TOTAL_UNREAD_MESSAGE_COUNT).document(youUid)

        firebase.runTransaction { transaction ->
            val meInfo = transaction.get(myInfo).toObject(UserEntity::class.java)
            val youInfo = transaction.get(youInfo).toObject(UserEntity::class.java)
            val youChatRoomExist = transaction.get(youChatRoom).exists()
            transaction.set(
                meChatRoom, ChatRoomEntity(
                    roomId = roomId, you = ChatRoomUserEntity(
                        profileImageUrl = youInfo?.profileImageUrl.toString(),
                        nickName = youInfo?.nickName.toString(),
                    )
                )
            )
            transaction.set(
                youChatRoom, ChatRoomEntity(
                    roomId = roomId, you = ChatRoomUserEntity(
                        profileImageUrl = meInfo?.profileImageUrl.toString(),
                        nickName = meInfo?.nickName.toString(),
                    )
                )
            )

//            if (!youChatRoomExist) {
//                transaction.set(
//                    yourMessageCountCol, mapOf(
//                        "totalUnReadMessageCount" to 0L
//                    ), SetOptions.merge()
//                )
//            }

        }.await()
    }


    override suspend fun updateLastVisitedTimeStamp(roomId: String) {
        Log.d("updateLastVisitedTimeStamp", "updateLastVisitedTimeStamp")
        val uid = auth.currentUser?.uid.toString()
        val myTotalUnReadMessageCountDoc =
            firebase.collection(COL_TOTAL_UNREAD_MESSAGE_COUNT).document(uid)
        val myChatRoomDoc =
            firebase.collection(COL_CHAT_ROOMS).document(uid).collection(COL_MY_CHAT_ROOMS)
                .document(roomId)

        val nowTime = FieldValue.serverTimestamp()

        firebase.runTransaction { transaction ->
            val unReadMessageCount =
                transaction.get(myChatRoomDoc).data?.get("unReadMessageCount") as? Long ?: 0L
            val totalUnReadMessageCount =
                transaction.get(myTotalUnReadMessageCountDoc).data?.get("totalUnReadMessageCount") as? Long
                    ?: 0L
            if (totalUnReadMessageCount > 0) {
                transaction.set(
                    myTotalUnReadMessageCountDoc, mapOf(
                        "totalUnReadMessageCount" to FieldValue.increment(
                            -unReadMessageCount
                        )
                    ), SetOptions.merge()
                )
            }
            transaction.set(
                myChatRoomDoc, mapOf(
                    "unReadMessageCount" to 0, "lastVisitedTimeStamp" to nowTime
                ), SetOptions.merge()
            )
        }.await()
    }

    override fun getTotalMessageCount(): Flow<ResultState<Long>> {
        val uid = auth.currentUser?.uid.toString()
        val myMessageCountCol = firebase.collection(COL_TOTAL_UNREAD_MESSAGE_COUNT).document(uid)
        return myMessageCountCol.snapshots().map { snapshot ->
            Log.d("getTotalMessageCount", "getTotalMessageCount>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            snapshot.getLong("totalUnReadMessageCount") ?: 0L
        }.map {
            ResultState.Success(it)
        }
    }

    override fun removeChatRoom(chatRoom: ChatRoomModel) = callbackFlow {
        trySend(ResultState.Loading)
        val uid = auth.currentUser?.uid.toString()
        val myChatRooms =
            firebase.collection(COL_CHAT_ROOMS).document(uid).collection(COL_MY_CHAT_ROOMS)
                .document(chatRoom.roomId)
        val myChatMessages = firebase.collection(COL_CHAT_MESSAGES).document(chatRoom.roomId)
        val totalUnReadCount = firebase.collection(COL_TOTAL_UNREAD_MESSAGE_COUNT).document(uid)
        firebase.runTransaction {
            val unReadCount = it.get(myChatRooms).getLong("unReadMessageCount") ?: 0L
            it.delete(myChatRooms)
            it.delete(myChatMessages)
            it.delete(
                myChatMessages.collection("contents").document()
            )

            it.update(
                totalUnReadCount, "totalUnReadMessageCount", FieldValue.increment(-unReadCount)
            )
        }.addOnSuccessListener {
            trySend(ResultState.Success(Unit))
        }.addOnFailureListener {
            it.printStackTrace()
            trySend(ResultState.Failure(it))
        }
        awaitClose {
            close()
        }
    }
}
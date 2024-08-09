package kr.sjh.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.getField
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.tasks.await
import kr.sjh.data.mapper.toChatMessageModel
import kr.sjh.data.mapper.toUserModel
import kr.sjh.data.model.ChatMessageEntity
import kr.sjh.data.model.UserEntity
import kr.sjh.data.utils.Constants
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatMessageModel
import kr.sjh.domain.model.ChatRoomModel
import kr.sjh.domain.repository.firebase.ChatRepository
import kr.sjh.domain.util.generateUniqueChatKey
import kr.sjh.domain.util.getReceiverUid
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firebase: FirebaseFirestore, private val auth: FirebaseAuth
) : ChatRepository {

    override fun getInitialMessages(
        roomId: String, size: Long
    ): Flow<ResultState<List<ChatMessageModel>>> = callbackFlow {
        Log.d("getInitialMessages", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        var listener: ListenerRegistration? =
            firebase.collection(Constants.COL_CHAT_MESSAGES).document(roomId).collection("contents")
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
        firebase.collection(Constants.COL_CHAT_MESSAGES).document(roomId).collection("contents")
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
            emit(ResultState.Loading) // 로딩 상태 emit
            auth.currentUser?.uid?.let { uid -> // 현재 사용자 UID 가져오기
                val roomId = generateUniqueChatKey(uid, message.receiverUid) // 채팅방 ID 생성
                val chatsDoc =
                    firebase.collection(Constants.COL_CHATS).document(roomId) // 채팅방 문서 참조

                val messagesCollection =
                    firebase.collection(Constants.COL_CHAT_MESSAGES) // 메시지 컬렉션 참조
                val messagesDoc = messagesCollection.document(roomId).collection("contents")
                    .document() // 새로운 메시지 문서 참조

                val partnerUserDoc =
                    firebase.collection(Constants.COL_USERS).document(message.receiverUid)

                val isRoomExist = chatsDoc.get().await().exists() // 채팅방 존재 여부 확인

                if (!isRoomExist) {
                    createChat(roomId, uid, message.receiverUid) // 채팅방이 존재하지 않으면 생성
                }

                val nowTime = FieldValue.serverTimestamp() // 서버 시간 가져오기

                // 트랜잭션을 사용하여 최근 메시지와 메시지 내용을 동시에 업데이트
                firebase.runTransaction { transaction ->
                    transaction.set(
                        chatsDoc, mapOf(
                            "recentMessageTimeStamp" to nowTime, // 최근 메시지 시간 업데이트
                            "recentMessage" to message.text, // 최근 메시지 내용 업데이트
                        ), SetOptions.merge() // 병합 옵션 사용
                    )
                    transaction.set(
                        messagesDoc, mapOf(
                            "messageId" to messagesDoc.id, // 메시지 ID 설정
                            "senderUid" to uid, // 보낸 사람 UID 설정
                            "receiverUid" to message.receiverUid, // 받는 사람 UID 설정
                            "message" to message.text, // 메시지 내용 설정
                            "timeStamp" to nowTime, // 메시지 시간 설정
                        ), SetOptions.merge() // 병합 옵션 사용
                    )
                }.await()


                //트랜잭션을 사용하여 읽지 않은 메시지 수 업데이트
                firebase.runTransaction { transaction ->
                    val lastVisitedTime = transaction.get(chatsDoc).get(
                        FieldPath.of(
                            message.receiverUid, "lastVisitedTimeStamp"
                        )
                    ) as Timestamp // 상대방의 마지막 방문 시간 가져오기

                    val recentMessageTime = transaction.get(chatsDoc)
                        .getTimestamp("recentMessageTimeStamp") // 최근 메시지 시간 가져오기
                    if (lastVisitedTime.toDate() < recentMessageTime?.toDate()) { // 마지막 방문 시간보다 최근 메시지 시간이 더 최근인 경우
                        transaction.set(
                            chatsDoc, mapOf(
                                message.receiverUid to mapOf(
                                    "unReadMessageCount" to FieldValue.increment(
                                        1
                                    ) // 읽지 않은 메시지 수 1 증가
                                )
                            ), SetOptions.merge() // 병합 옵션 사용
                        )
                        transaction.set(
                            partnerUserDoc, mapOf(
                                "totalUnReadMessageCount" to FieldValue.increment(
                                    1
                                )
                            ), SetOptions.merge()
                        )
                    }
                }.await()

                emit(ResultState.Success(Unit)) // 성공 상태 emit

            }
        } catch (e: Exception) {
            e.printStackTrace() // 예외 발생 시 스택 트레이스 출력
            emit(ResultState.Failure(e)) // 실패 상태 emit
        }
    }


    override fun getChatRooms(): Flow<ResultState<List<ChatRoomModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        val uid = auth.currentUser?.uid.toString()
        val userDoc = firebase.collection(Constants.COL_USERS).document(uid)
        val messageCol = firebase.collection(Constants.COL_CHATS)
        val myChatRooms =
            userDoc.get(Source.SERVER).await().get("chatRooms") as? List<*> ?: emptyList<String>()
        Log.d("sjh", "myChatRooms size : ${myChatRooms.size}")

        if (myChatRooms.isEmpty()) {
            trySend(ResultState.Success(emptyList()))
            awaitCancellation()
        }

        val listener = messageCol.whereIn(FieldPath.documentId(), myChatRooms)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ResultState.Failure(error))
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val chatRooms =
                        snapshot.documents.filter { myChatRooms.contains(it.id) }.map { document ->
                            val partnerUid = getReceiverUid(document.id, uid)
                            val userData = document.data?.get(uid) as Map<String, Any>
                            val partnerData = document.data!![partnerUid] as Map<String, Any>
                            ChatRoomModel(
                                roomId = document.id,
                                recentMessage = document.getString("recentMessage") ?: "",
                                recentMessageTimeStamp = document.getTimestamp("recentMessageTimeStamp")
                                    ?.toDate(),
                                users = mapOf(
                                    uid to userData, partnerUid to partnerData
                                )
                            )
                        }
                    Log.d("sjh", "chatRooms : ${chatRooms.size}")
                    trySend(ResultState.Success(chatRooms))
                }
            }

        awaitClose {
            Log.d("getChatRooms", "getChatRooms close")
            listener.remove()
            close()
        }
    }

    private suspend fun createChat(
        roomId: String, user1Uid: String, user2Uid: String
    ) {
        val chatsDoc = firebase.collection(Constants.COL_CHATS).document(roomId)
        val user1Doc = firebase.collection(Constants.COL_USERS).document(user1Uid)
        val user2Doc = firebase.collection(Constants.COL_USERS).document(user2Uid)
        firebase.runTransaction { transaction ->
            val user1 = transaction.get(user1Doc).toObject(UserEntity::class.java)
            val user2 = transaction.get(user2Doc).toObject(UserEntity::class.java)
            transaction.set(
                chatsDoc, mapOf(
                    user1Uid to mapOf(
                        "profileImageUrl" to user1?.profileImageUrl,
                        "nickName" to user1?.nickName,
                        "unReadMessageCount" to 0,
                        "lastVisitedTimeStamp" to FieldValue.serverTimestamp()
                    ),
                    user2Uid to mapOf(
                        "profileImageUrl" to user2?.profileImageUrl,
                        "nickName" to user2?.nickName,
                        "unReadMessageCount" to 0,
                        "lastVisitedTimeStamp" to FieldValue.serverTimestamp(),
                    ),
                    "recentMessage" to "",
                    "recentMessageTimeStamp" to FieldValue.serverTimestamp()
                )
            )
            transaction.set(
                user1Doc, mapOf(
                    "chatRooms" to FieldValue.arrayUnion(roomId)
                ), SetOptions.merge()
            )
            transaction.set(
                user2Doc, mapOf(
                    "chatRooms" to FieldValue.arrayUnion(roomId)
                ), SetOptions.merge()
            )
        }.await()
    }

    override suspend fun updateLastVisitedTimeStamp(roomId: String) {
        Log.d("updateLastVisitedTimeStamp", "updateLastVisitedTimeStamp")
        val uid = auth.currentUser?.uid.toString()
        val chatDoc = firebase.collection(Constants.COL_CHATS).document(roomId)
        val userDoc = firebase.collection(Constants.COL_USERS).document(uid)
        firebase.runTransaction { transaction ->
            val exists = transaction.get(chatDoc).exists()
            val user = transaction.get(userDoc).toObject(UserEntity::class.java)
            val unReadMessageCount =
                transaction.get(chatDoc).get(FieldPath.of(uid, "unReadMessageCount")) as Long
            if (user != null && user.totalUnReadMessageCount > 0) {
                transaction.set(
                    userDoc, mapOf(
                        "totalUnReadMessageCount" to FieldValue.increment(-unReadMessageCount)
                    ), SetOptions.merge()
                )
            }
            if (exists) {
                transaction.set(
                    chatDoc, mapOf(
                        uid to mapOf(
                            "lastVisitedTimeStamp" to FieldValue.serverTimestamp(),
                            "unReadMessageCount" to 0
                        )
                    ), SetOptions.merge()
                )
            }

        }
    }

    override fun getTotalUnReadMessageCount(uid: String): Flow<ResultState<Long>> = flow {}
}
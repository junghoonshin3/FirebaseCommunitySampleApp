package kr.sjh.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
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
import kr.sjh.domain.model.UserModel
import kr.sjh.domain.repository.firebase.ChatRepository
import kr.sjh.domain.util.generateUniqueChatKey
import java.util.Date
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firebase: FirebaseFirestore
) : ChatRepository {

    override fun getInitialMessages(
        roomId: String, limit: Long
    ): Flow<ResultState<ChatMessageModel>> = callbackFlow {
        val listener = firebase.collection(Constants.FirebaseCollectionChats).document(roomId)
            .collection(Constants.FirebaseCollectionChatMessages)
            .orderBy("timeStamp", Query.Direction.DESCENDING).limit(limit)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(ResultState.Failure(error))
                }
                if (value != null) {
                    if (!value.isEmpty) {
                        value.documentChanges.map { dc ->
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

    override fun getNextMessages(
        roomId: String, limit: Long, fromTime: Long
    ): Flow<ResultState<List<ChatMessageModel>>> = callbackFlow {

        // fromTime을 Timestamp 객체로 변환
        val timestamp = Timestamp(fromTime / 1000, (fromTime % 1000 * 1000000).toInt())

        firebase.collection(Constants.FirebaseCollectionChats).document(roomId)
            .collection(Constants.FirebaseCollectionChatMessages)
            .whereLessThan("timeStamp", timestamp).orderBy("timeStamp", Query.Direction.DESCENDING)
            .limit(limit).get().addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val messages = snapshot.toObjects(ChatMessageEntity::class.java).map {
                        it.toChatMessageModel()
                    }
                    trySend(ResultState.Success(messages))
                    // 마지막 문서 업데이트
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
        chatMessageModel: ChatMessageModel
    ): Flow<ResultState<Unit>> = flow {
        try {
            val roomId =
                generateUniqueChatKey(chatMessageModel.senderUid, chatMessageModel.receiverUid)
            val sendDoc = firebase.collection(Constants.FirebaseCollectionUsers)
                .document(chatMessageModel.senderUid)
            val receiveDoc = firebase.collection(Constants.FirebaseCollectionUsers)
                .document(chatMessageModel.receiverUid)
            val chatMessagesDoc =
                firebase.collection(Constants.FirebaseCollectionChats).document(roomId)
                    .collection(Constants.FirebaseCollectionChatMessages).document()
            val chatDoc = firebase.collection(Constants.FirebaseCollectionChats).document(roomId)

            firebase.runTransaction { transaction ->
                transaction.update(sendDoc, "myChats", FieldValue.arrayUnion(roomId))
                transaction.update(receiveDoc, "myChats", FieldValue.arrayUnion(roomId))
                transaction.set(
                    chatMessagesDoc,
                    chatMessageModel.toChatMessageEntity().copy(messageId = chatMessagesDoc.id)
                )
            }.await()

            // 최근메세지 내용 갱신
            val recentMessage =
                chatMessagesDoc.get().await().toObject(ChatMessageEntity::class.java)
            chatDoc.set(ChatRoomModel(recentMessage?.message.toString(), recentMessage?.timeStamp))
                .await()
            emit(ResultState.Success(Unit))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ResultState.Failure(e))
        }
    }

    override fun getChatRooms(uid: String): Flow<ResultState<List<ChatMessageModel>>> =
        callbackFlow {

            awaitClose {
                close()
            }
        }

}
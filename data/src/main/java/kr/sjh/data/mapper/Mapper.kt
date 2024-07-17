package kr.sjh.data.mapper

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import kr.sjh.data.model.ChatMessageEntity
import kr.sjh.data.model.PostEntity
import kr.sjh.data.model.UserEntity
import kr.sjh.domain.model.AuthUserModel
import kr.sjh.domain.model.ChatMessageModel
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.model.UserModel
import java.util.Date

fun PostModel.toPostEntity(): PostEntity {
    return PostEntity(
        writerUid = writerUid,
        postKey = postKey,
        title = title,
        content = content,
        nickName = nickName,
        readCount = readCount,
        likeCount = likeCount,
        images = images
    )
}

fun PostEntity.toPostModel(): PostModel {
    return PostModel(
        writerUid = writerUid,
        postKey = postKey,
        title = title,
        content = content,
        nickName = nickName,
        readCount = readCount,
        likeCount = likeCount,
        timeStamp = timeStamp?.toDate() ?: Date(),
        images = images
    )
}

fun UserModel.toUserEntity(): UserEntity {
    return UserEntity(
        uid = uid,
        nickName = nickName,
        profileImageUrl = profileImageUrl,
        likePosts = likePosts,
        myPosts = myPosts,
        role = role,
        myChats = myChats
    )
}

fun UserEntity.toUserModel(): UserModel {
    return UserModel(
        uid = uid,
        nickName = nickName,
        profileImageUrl = profileImageUrl,
        likePosts = likePosts,
        myPosts = myPosts,
        role = role,
        myChats = myChats
    )
}


fun FirebaseUser.toAuthUserModel() = AuthUserModel(
    email = email ?: "email is not exist",
    profileImageUrl = photoUrl.toString(),
    uid = uid,
    nickName = displayName ?: "Unknown User",
)

fun ChatMessageModel.toChatMessageEntity() = ChatMessageEntity(
    messageId = messageId,
    senderUid = senderUid,
    receiverUid = receiverUid,
    profileImageUrl = profileImageUrl,
    message = message,
)

fun ChatMessageEntity.toChatMessageModel() = ChatMessageModel(
    messageId = messageId,
    senderUid = senderUid,
    receiverUid = receiverUid,
    profileImageUrl = profileImageUrl,
    message = message,
    timeStamp = timeStamp
)


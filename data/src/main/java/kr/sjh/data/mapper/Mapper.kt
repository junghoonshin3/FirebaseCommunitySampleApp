package kr.sjh.data.mapper

import com.google.firebase.auth.FirebaseUser
import kr.sjh.data.model.PostEntity
import kr.sjh.data.model.UserEntity
import kr.sjh.domain.model.AuthUserModel
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.model.UserModel

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

fun UserModel.toUserEntity(): UserEntity {
    return UserEntity(
        uid = uid,
        nickName = nickName,
        profileImageUrl = profileImageUrl,
        likePosts = likePosts,
        myPosts = myPosts
    )
}

fun UserEntity.toUserModel(): UserModel {
    return UserModel(
        uid = uid,
        nickName = nickName,
        profileImageUrl = profileImageUrl,
        likePosts = likePosts,
        myPosts = myPosts
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
        timeStamp = timeStamp,
        images = images
    )
}

fun FirebaseUser.toAuthUserModel() = AuthUserModel(
    email = email ?: "email is not exist",
    profileImageUrl = photoUrl.toString(),
    uid = uid,
    nickName = displayName ?: "Unknown User"
)

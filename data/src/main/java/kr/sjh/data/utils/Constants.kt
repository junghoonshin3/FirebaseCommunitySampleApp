package kr.sjh.data.utils

object Constants {

    const val FirebaseCollectionUsers = "users"
    const val FirebaseCollectionPosts = "posts"
    const val FirebaseStorageProfileImages = "profile_images"
    const val FirebaseStoragePostImages = "post_images"

    object ErrorCode {
        const val ERROR_INVALID_CREDENTIAL = "ERROR_INVALID_CREDENTIAL"
        const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
        const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
    }

}
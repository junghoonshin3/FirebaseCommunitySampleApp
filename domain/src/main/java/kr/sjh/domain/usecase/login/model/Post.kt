package kr.sjh.domain.usecase.login.model

data class Post(
    var id: String = "",
    var key: String = "",
    var title: String? = null,
    var content: String? = null,
    var nickName: String? = null,
    var createdAt: Long? = null,
    var imageUrl: String? = null
)
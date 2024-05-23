package kr.sjh.domain.error

class NotFoundUser(
    override val message: String = "Not Found User"
) : RuntimeException()
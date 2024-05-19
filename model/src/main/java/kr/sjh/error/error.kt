package kr.sjh.error

class NotFoundUser(
    override val message: String = "Not Found User"
) : RuntimeException()
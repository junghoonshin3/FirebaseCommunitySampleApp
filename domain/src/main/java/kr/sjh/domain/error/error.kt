package kr.sjh.domain.error

class NotFoundUser constructor(
    override val message: String = "Not Found User"
) : RuntimeException()
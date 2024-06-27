package kr.sjh.domain.usecase.auth.firebase

import kr.sjh.domain.model.AuthUserModel

fun interface GetAuthCurrentUserUseCase {
    operator fun invoke(): AuthUserModel?
}
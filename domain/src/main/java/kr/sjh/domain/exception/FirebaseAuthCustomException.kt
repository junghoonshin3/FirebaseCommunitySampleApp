package kr.sjh.domain.exception

sealed class FirebaseAuthCustomException : RuntimeException() {
    // 사용자가 로그인을 시도할 때 입력한 인증 정보가 유효하지않음
    data class UserNotFoundException(override val message: String? = "파이어 베이스에서 사용자를 찾을수 없습니다.") : FirebaseAuthCustomException()

    // FireStore Users 컬렉션에 등록된 사용자 정보가 없음
    data class UserNotFoundInUsers(override val message: String? = "사용자를 찾을수 없습니다.") :
        FirebaseAuthCustomException()

    //  이미 이메일이 등록된 경우
    data object EmailAlreadyInUse : FirebaseAuthCustomException()
    data object FirebaseAuthWeakPasswordException : FirebaseAuthCustomException()

}
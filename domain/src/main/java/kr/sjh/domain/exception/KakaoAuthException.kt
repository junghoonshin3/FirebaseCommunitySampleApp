package kr.sjh.domain.exception

sealed class KakaoAuthException : Exception() {

    //카카오 로그인 토큰이 잘못된 경우
    data object KakaoInavildTokenException : KakaoAuthException()

    //카카오 토큰이 없는 경우
    data object KakaoTokenNotFoundExceptionKakao : KakaoAuthException()
}
package kr.sjh.domain.util

/**
 * 두 UID를 받아서 항상 동일한 유니크한 키를 생성합니다.
 * @param uid1 본인의 UID
 * @param uid2 상대방의 UID
 * @return 유니크한 키
 */
fun generateUniqueChatKey(uid1: String, uid2: String): String {
    return if (uid1 < uid2) {
        "${uid1}_${uid2}"
    } else {
        "${uid2}_${uid1}"
    }
}

fun getReceiverUid(roomId: String, uid: String): String {
    val separatedIds = roomId.split("_")
    return if (separatedIds[0] == uid) {
        separatedIds[1] // 현재 사용자 UID가 첫 번째 요소이면 두 번째 요소 반환
    } else {
        separatedIds[0] // 그렇지 않으면 첫 번째 요소 반환
    }
}
package kr.sjh.domain.usecase.login.model

import android.net.Uri
import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
data class UserInfo(
    val email: String? = null,
    val nickName: String? = null,
    val id: String? = null,
    val profileImageUrl: String? = null,
    val postCount: Int = 0,
    var likePosts: MutableList<String> = mutableListOf()
) : Parcelable {
    override fun toString(): String {
        return Uri.encode(Gson().toJson(this))
    }
}
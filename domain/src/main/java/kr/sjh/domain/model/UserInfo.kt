package kr.sjh.domain.model

import android.net.Uri
import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
@IgnoreExtraProperties
data class UserInfo(
    val email: String? = null,
    var nickName: String? = null,
    val id: String? = null,
    val profileImageUrl: String? = null,
    val postCount: Int = 0,
    val likePosts: List<String> = emptyList()
) : Parcelable {
    override fun toString(): String {
        return Uri.encode(Gson().toJson(this))
    }
}
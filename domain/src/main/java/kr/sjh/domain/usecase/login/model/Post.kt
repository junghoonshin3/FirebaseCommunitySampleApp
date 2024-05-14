package kr.sjh.domain.usecase.login.model

import android.net.Uri
import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize


@Parcelize
@Stable
data class Post(
    val writerId: String = "",
    var key: String = "",
    var title: String? = null,
    var content: String? = null,
    val nickName: String? = null,
    val createdAt: Long? = null,
    var imageUrl: String? = null,
    var readCount: Int = 0,
    var likeCount: Int = 0
) : Parcelable {
    override fun toString(): String {
        return Uri.encode(Gson().toJson(this))
    }


}
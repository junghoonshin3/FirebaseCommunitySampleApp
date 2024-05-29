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
data class Post(
    val writerId: String = "",
    var key: String = "",
    var title: String? = null,
    var content: String? = null,
    val nickName: String? = null,
    val createdAt: Long? = null,
    var readCount: Int = 0,
    var likeCount: Int = 0,
    val images: List<String> = listOf()
) : Parcelable {
    override fun toString(): String {
        return Uri.encode(Gson().toJson(this))
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "writerId" to writerId,
            "key" to key,
            "title" to title,
            "content" to content,
            "nickName" to nickName,
            "createdAt" to createdAt,
            "readCount" to readCount,
            "likeCount" to likeCount,
            "images" to images,
        )
    }

}
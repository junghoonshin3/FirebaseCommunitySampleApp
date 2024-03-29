package kr.sjh.presentation.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import kr.sjh.domain.usecase.login.model.Post


class PostType : NavType<Post>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Post? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): Post {
        return Gson().fromJson(value, Post::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: Post) {
        bundle.putParcelable(key, value)
    }
}
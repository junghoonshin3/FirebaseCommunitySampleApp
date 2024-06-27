package kr.sjh.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtil {
    fun isLocalUri(uri: Uri): Boolean {
        return when (uri.scheme) {
            "file", "content" -> true
            else -> false
        }
    }

    suspend fun downloadImageFromUrl(context: Context, url: String): Uri {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response: Response = client.newCall(request).execute()

                if (!response.isSuccessful) throw Exception("Failed to download file: $response")

                val file = File(context.cacheDir, "profile_image.jpg")
                val fos = FileOutputStream(file)
                fos.use { fosStream ->
                    response.body?.byteStream()?.use { inputStream ->
                        inputStream.copyTo(fosStream)
                    }
                }

                file.toUri()
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    suspend fun resizeImage(
        context: Context,
        uid: String,
        imageUri: Uri,
        width: Int,
        height: Int
    ): Uri {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                    ?: throw Exception("Failed to decode bitmap")

                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

                val file = File(context.cacheDir, "${uid}_profile_resize_image.jpg")
                val fos = FileOutputStream(file)
                fos.use { fosStream ->
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fosStream)
                }
                file.toUri()
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }


}
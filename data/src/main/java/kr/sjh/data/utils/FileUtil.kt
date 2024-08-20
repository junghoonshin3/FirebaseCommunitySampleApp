package kr.sjh.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FileUtil(val context: Context) {

    fun isLocalUri(uri: Uri): Boolean {
        return when (uri.scheme) {
            "file", "content" -> true
            else -> false
        }
    }

    suspend fun downloadImageFromUrl(
        imageUri: Uri
    ): Uri {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(imageUri.toString()).build()
                val response: Response = client.newCall(request).execute()

                if (!response.isSuccessful) throw Exception("Failed to download file: $response")
                val file = File(context.cacheDir, "${imageUri.lastPathSegment}")
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

    fun optimizedBitmap(
        imageUri: Uri, reqWidth: Int, reqHeight: Int
    ): Bitmap {
        return try {
            var inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                ?: throw Exception("Failed to open input stream")

            val options = BitmapFactory.Options()

            inputStream.use {
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, options)?.run {
                    recycle()
                }
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            }
            inputStream?.close()

            options.inJustDecodeBounds = false

            inputStream = context.contentResolver.openInputStream(imageUri)

            val resizedBitmap = inputStream.use {
                BitmapFactory.decodeStream(inputStream, null, options)
                    ?: throw Exception("Failed to decode bitmap")
            }
            inputStream?.close()

            inputStream = context.contentResolver.openInputStream(imageUri)
                ?: throw Exception("Failed to open input stream")

            val rotationBitmap = inputStream.use {
                rotateImageIfRequired(resizedBitmap, imageUri)
                    ?: throw Exception("Failed to rotate image")
            }

//            resizedBitmap.recycle()
            inputStream.close()
            rotationBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (width > reqWidth || height > reqHeight) {
            val halfWidth = width / 2
            val halfHeight = height / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        Log.d("calculateInSampleSize", "inSampleSize >>>>  $inSampleSize")
        return inSampleSize
    }

    private fun rotateImageIfRequired(bitmap: Bitmap, uri: Uri): Bitmap? {
        val input = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Failed to open rotateImageIfRequired")

        return input.use {
            val exif = ExifInterface(input)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
                else -> bitmap
            }
        }
    }

    private fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun saveBitmapAsFile(bitmap: Bitmap, fileName: String): File {
        val cacheDir = context.cacheDir
        val file = File(cacheDir, fileName)
        try {
            //빈 파일 생성
            file.createNewFile()

            //파일을 쓸수있는 스트림 생성
            val out = FileOutputStream(file)
            out.use {
                //스트림에 비트맵 저장
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file
    }
}
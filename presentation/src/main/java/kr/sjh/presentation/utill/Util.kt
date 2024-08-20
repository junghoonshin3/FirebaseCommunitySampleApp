package kr.sjh.presentation.utill

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import coil.decode.DecodeUtils.calculateInSampleSize
import coil.size.Scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

fun calculationTime(createDateTime: Long): String {
    val nowDateTime = System.currentTimeMillis() //현재 시간 to millisecond
    var value = ""
    val differenceValue = nowDateTime - createDateTime //현재 시간 - 비교가 될 시간
    when {
        differenceValue < 60000 -> { //59초 보다 적다면
            value = "방금 전"
        }

        differenceValue < 3600000 -> { //59분 보다 적다면
            value = TimeUnit.MILLISECONDS.toMinutes(differenceValue).toString() + "분 전"
        }

        differenceValue < 86400000 -> { //23시간 보다 적다면
            value = TimeUnit.MILLISECONDS.toHours(differenceValue).toString() + "시간 전"
        }

        differenceValue < 604800000 -> { //7일 보다 적다면
            value = TimeUnit.MILLISECONDS.toDays(differenceValue).toString() + "일 전"
        }

        differenceValue < 2419200000 -> { //3주 보다 적다면
            value = (TimeUnit.MILLISECONDS.toDays(differenceValue) / 7).toString() + "주 전"
        }

        differenceValue < 31556952000 -> { //12개월 보다 적다면
            value = (TimeUnit.MILLISECONDS.toDays(differenceValue) / 30).toString() + "개월 전"
        }

        else -> { //그 외
            value = (TimeUnit.MILLISECONDS.toDays(differenceValue) / 365).toString() + "년 전"
        }
    }
    return value
}

fun optimizedBitmap(
    context: Context,
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
            options.inSampleSize =
                calculateInSampleSize(
                    options.outWidth,
                    options.outHeight,
                    reqWidth,
                    reqHeight,
                    scale = Scale.FIT
                )

        }

        options.inJustDecodeBounds = false

        inputStream = context.contentResolver.openInputStream(imageUri)

        inputStream.use {
            BitmapFactory.decodeStream(inputStream, null, options)
                ?: throw Exception("Failed to decode bitmap")
        }

//            inputStream = context.contentResolver.openInputStream(imageUri)
//                ?: throw Exception("Failed to open input stream")
//
//            val rotationBitmap = inputStream.use {
//                rotateImageIfRequired(resizedBitmap, imageUri)
//                    ?: throw Exception("Failed to rotate image")
//            }
//
//            resizedBitmap.recycle()
//
//            rotationBitmap
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }
}


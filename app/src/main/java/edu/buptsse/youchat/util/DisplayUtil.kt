package edu.buptsse.youchat.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun Context.getScreenWidth(): Dp {
    val px = this.resources.displayMetrics.widthPixels
    val dpi = this.resources.displayMetrics.densityDpi
    return (px * 160 / dpi).dp
}

fun Context.getScreenHeight(): Dp {
    val px = this.resources.displayMetrics.heightPixels
    val dpi = this.resources.displayMetrics.densityDpi
    return (px * 160 / dpi).dp
}

fun Context.getBitmapFromUri(maxHeight: Int, maxWidth: Int, uri: Uri): Bitmap {
    val bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, uri))
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    val options = BitmapFactory.Options()
    // 压缩图片尺寸 尺寸小于maxHeight * maxWidth
    var inSampleSize = 1
    while (bitmap.height / inSampleSize > maxHeight && bitmap.width / inSampleSize > maxWidth) {
        inSampleSize *= 2
    }
    options.inSampleSize = inSampleSize
    val inputStream = ByteArrayInputStream(outputStream.toByteArray())
    return BitmapFactory.decodeStream(inputStream, null, options)!!
}
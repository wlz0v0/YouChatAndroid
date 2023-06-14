package edu.buptsse.youxuancheng.util

import android.content.Context
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
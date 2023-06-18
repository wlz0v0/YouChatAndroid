package edu.buptsse.youchat.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

private const val hasPermission = PackageManager.PERMISSION_GRANTED

fun Context.hasReadAndWriteStoragePermission(): Boolean {
    val readPermission = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == hasPermission
    val writePermission = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == hasPermission
    return (readPermission && writePermission)
}

fun Context.hasMicrophonePermission(): Boolean {
    return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == hasPermission
}

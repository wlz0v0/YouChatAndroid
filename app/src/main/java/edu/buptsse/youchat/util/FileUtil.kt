package edu.buptsse.youchat.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

var externalFilePath = ""
var loginDataPath = ""
const val STORAGE_REQUEST_CODE = 23216

fun Context.initConst() {
    externalFilePath = this.getExternalFilesDir(null)!!.absolutePath
    loginDataPath = "$externalFilePath/login.dat"
    createFileIfNotExists(loginDataPath)
}

fun createAllFiles() {
    createFileIfNotExists(loginDataPath)
}

/**
 * 根据文件路径创建文件
 *
 * @param path 文件路径
 */
fun createFileIfNotExists(path: String, init: String? = null): File {
    val index = path.indexOfLast { it == '/' }
    assert(index != -1)
    // 先解析出文件的目录
    val dir = File(path.substring(0, index))
    // 不存在目录则创建
    if (!dir.exists()) {
        dir.mkdirs()
    }
    // 创建文件对象
    val file = File(path)
    // 不存在则创建，存在则直接返回对象
    if (!file.exists()) {
        Log.i("path", file.absolutePath)
        file.createNewFile()
        init?.let {
            FileOutputStream(file).use { fos ->
                fos.write(init.toByteArray())
                fos.flush()
            }
        }
    }
    return file
}
package edu.buptsse.youchat.util

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
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

/**
 * Title: FileUri
 * <p>
 * Description: Uri 工具类
 * </p>
 * @author javakam
 * @date 2020/8/24  11:24
 */
object FileUri {
    //获取Uri对应的文件路径 兼容API 26
    //----------------------------------------------------------------
    /**
     * 根据uri获取文件的绝对路径，解决Android 4.4以上 根据uri获取路径的方法
     *
     * <p>
     *      RequiresPermission(permission.READ_EXTERNAL_STORAGE)
     * @param context
     * @param uri
     * @return
     */
    fun getFilePathByUri(context: Context?, uri: Uri?): String? {
        if (context == null || uri == null) return null
        val scheme = uri.scheme
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE.equals(scheme, ignoreCase = true)) {//使用第三方应用打开
            uri.path
        }
        return getPath(context, uri)
    }

    /**
     * Get a file path from a Uri. This will get the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br></br>
     * <br></br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * <pre>
     * 错误的方式 -> 采用复制文件的方式重新写入再获取路径
     * https://stackoverflow.com/questions/42508383/illegalargumentexception-column-data-does-not-exist
     * @param context The context.
     * @param uri     The Uri to query.
     * @see #isLocal
     * @see #getFile
     */
    private fun getPath(context: Context, uri: Uri?): String? {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory()
                        .toString() + File.separator + split[1]
                } else if ("home".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory()
                        .toString() + File.separator + "documents" + File.separator + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4)
                }
                // Android Q 待验证
                val contentUriPrefixesToTry = arrayOf(
                    "content://downloads/public_downloads",
                    "content://downloads/my_downloads",
                    "content://downloads/all_downloads"
                )
                for (contentUriPrefix in contentUriPrefixesToTry) {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse(contentUriPrefix),
                        id.toLong()
                    )
                    try {
                        val path = getDataColumn(context, contentUri, null, null)
                        if (!TextUtils.isEmpty(path)) {
                            return path
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                return uri?.path
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri?.scheme, ignoreCase = true)) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri?.lastPathSegment
            } else if (isHuaWeiUri(uri)) {
                val uriPath = uri?.path
                //content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/Android/data/com.xxx.xxx/
                if (uriPath != null && uriPath.startsWith("/root")) {
                    return uriPath.replace("/root".toRegex(), "")
                }
            }
            return uri?.path
        } else if ("file".equals(uri?.scheme, ignoreCase = true)) {
            uri?.path
        }
        return uri?.path
    }

    /**
     * <pre>
     * 两个BUG :
     * 1.Android 10 闪退问题
     * 2.部分机型进入"文件管理器" 执行到  cursor.getColumnIndexOrThrow(column);出现
     * Caused by: java.lang.IllegalArgumentException: column '_data' does not exist. Available columns: []
     * Fixed :
     * FileChooserPathUtils 注释
     * 和
     * https://stackoverflow.com/questions/42508383/illegalargumentexception-column-data-does-not-exist
     * </pre>
     */
    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        val column = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(column)
        context.contentResolver.query(
            uri ?: return null,
            projection,
            selection,
            selectionArgs,
            null
        )?.use {
            try {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(column)
                    return it.getString(columnIndex)
                } else uri.path
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return uri.path
    }

    //The Uri to check
    //----------------------------------------------------------------

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri?): Boolean {
        return "com.google.android.apps.photos.content" == uri?.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri?): Boolean {
        return "com.android.externalstorage.documents" == uri?.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri?): Boolean {
        return "com.android.providers.downloads.documents" == uri?.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri?): Boolean {
        return "com.android.providers.media.documents" == uri?.authority
    }

    /**
     * content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/Android/data/com.xxx.xxx/
     *
     * @param uri
     * @return
     */
    private fun isHuaWeiUri(uri: Uri?): Boolean {
        return "com.huawei.hidisk.fileprovider" == uri?.authority
    }

}
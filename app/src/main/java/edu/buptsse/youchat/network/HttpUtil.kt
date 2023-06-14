@file:Suppress("HttpUrlsUsage", "unused")

package edu.buptsse.youxuancheng.network

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.URL

/**
 * 所有API的url
 */
const val SERVER_URL = "http://10.0.2.2:8888/rpc"
const val NO_CONNECTION = "369"
inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type


/**
 * 获取网络类型
 *
 * @return 1-wifi, 2-流量, 369-无连接
 */
@Suppress("DEPRECATION")
fun Context.getNetworkType(): Int {
    val connectivity = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = connectivity.activeNetworkInfo ?: return NO_CONNECTION.toInt()
    return when (info.type) {
        ConnectivityManager.TYPE_WIFI -> 1
        ConnectivityManager.TYPE_MOBILE -> 2
        else -> NO_CONNECTION.toInt()
    }
}

suspend fun getCommodities() {

}

suspend fun getExpress(asin: String) {

}

/**
 * Post方法，传递参数获取结果
 *
 * @param urlStr url地址字符串
 * @param params post参数
 * @return 结果
 */
suspend fun post(urlStr: String, params: Map<String, String>): String {
    var s: StringBuilder? = null

    try {
        withContext(Dispatchers.IO) {
            val url = URL(urlStr)
            // 耗时操作
            val conn = url.openConnection() as HttpURLConnection
            Log.e("url", urlStr)
            conn.requestMethod = "POST"
            //以下两行必须加否则报错.
            conn.doInput = true
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")

            conn.connect()
            // 发送参数
            BufferedWriter(OutputStreamWriter(conn.outputStream)).use { writer ->
                val gson = Gson()
                val res = gson.toJson(params)
                Log.e("param", res)
                writer.write(res)
                writer.flush()
            }

            // 获取结果
            if (conn.responseCode > 400) BufferedReader(InputStreamReader(conn.errorStream))
            else BufferedReader(InputStreamReader(conn.inputStream)).use { reader ->
                var line: String?
                s = StringBuilder()
                while (reader.readLine().also { line = it } != null) {
                    s?.append(line)
                }
            }
        }
    } catch (e: ProtocolException) {
        // avd自带bug，最后一行读不到，给结果补上最后一行
//        when (urlStr) {
//            GET_CONTACT_URL -> s?.append("}")
//            GET_TEMPLATES -> s?.append("}")
//            SEND_EMAIL_URL -> s?.append("True")
//            else -> s?.append("1")
//        }
        Log.e("avd err", "unexpected end of stream")
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    // null因为猪头把服务器给关了
    if (s == null || s.toString() == "") {
        Log.e("server", "no connection")
        return NO_CONNECTION
    }
    return s.toString()
}

/**
 * Get方法，获取相应url的数据
 *
 * @param urlStr url地址字符串
 * @return 数据
 */
suspend fun get(urlStr: String): String {
    var s: StringBuilder? = null
    try {
        withContext(Dispatchers.IO) {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            var line: String?
            // 获取结果
            BufferedReader(InputStreamReader(conn.inputStream)).use { reader ->
                s = StringBuilder()
                while (reader.readLine().also { line = it } != null) {
                    s?.append(line)
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    if (s == null || s.toString() == "") {
        // null因为猪头把服务器给关了
        Log.e("server", "no connection")
        return NO_CONNECTION
    }
    return s.toString()
}
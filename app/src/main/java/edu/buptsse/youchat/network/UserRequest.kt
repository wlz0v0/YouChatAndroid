package edu.buptsse.youxuancheng.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import edu.buptsse.youchat.domain.User
import edu.buptsse.youchat.main.curUser
import edu.buptsse.youchat.main.token
import edu.buptsse.youchat.util.createFileIfNotExists
import edu.buptsse.youchat.util.loginDataPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.MessageDigest

private const val USER_URL = "$SERVER_URL/user"
private const val LOGIN_URL = "$USER_URL/login"
private const val TOKEN_LOGIN_URL = "$USER_URL/token_login"
private const val REGISTER_URL = "$USER_URL/register"
private const val CHANGE_PWD_URL = "$USER_URL/changePassword"
private const val SET_PWD_URL = "$USER_URL/setPassword"

fun getUser(id: String): User {
    return User(id, "伍昶旭", "")
}

/**
 * 调用登录服务
 *
 * @param phoneNumber 账号
 * @param password    密码
 * @return 1-登录成功, 0-密码错误, -1-用户名不存在, 369-网络错误
 */
suspend fun login(phoneNumber: String, password: String): Int {
    val cipherText = passwordEncrypt(password)
    val map = HashMap<String, String>()
    map["userId"] = phoneNumber
    map["pwd"] = password
    map["ttl"] = "7"
    val res = post(LOGIN_URL, map)
    Log.e("res", res)
    if (res == "369") {
        return res.toInt()
    }
    val gson = Gson()
    val response = gson.fromJson(res, Response::class.java)
    return if (response.error()) {
        response.code - 360
    } else {
        val json = JsonParser.parseString(response.result).asJsonObject
        token = json["token"].asString
        curUser = gson.fromJson(json["user"].asString, User::class.java)
        createFileIfNotExists(loginDataPath)
        withContext(Dispatchers.IO) {
            FileOutputStream(File(loginDataPath)).use {
                it.write("$token\n".toByteArray())
                it.write(curUser.id.toByteArray())
                it.flush()
            }
        }
        return 0
    }
}

suspend fun loginByToken(phoneNumber: String, token: String): Int {
    val map = HashMap<String, String>()
    map["userId"] = phoneNumber
    map["token"] = token
    map["ttl"] = "7"
    val res = post(TOKEN_LOGIN_URL, map)
    Log.e("res", res)
    if (res == "369") {
        return res.toInt()
    }
    val gson = Gson()
    val response = gson.fromJson(res, Response::class.java)
    return if (response.error()) {
        response.code - 360
    } else {
        val json = JsonParser.parseString(response.result).asJsonObject
        edu.buptsse.youchat.main.token = json["token"].asString
        curUser = gson.fromJson(json["user"].asString, User::class.java)
        createFileIfNotExists(loginDataPath)
        withContext(Dispatchers.IO) {
            FileOutputStream(File(loginDataPath)).use {
                it.write("${edu.buptsse.youchat.main.token}\n".toByteArray())
                it.write(curUser.id.toByteArray())
                it.flush()
            }
        }
        return 0
    }
}

suspend fun isVerificationCodeValid(): Boolean {
    return true
}

/**
 * 采用sha算法加密密码
 *
 * @param plainText 明文
 * @return 密文
 */
private fun passwordEncrypt(plainText: String): String {
    val messageDigest = MessageDigest.getInstance("sha")
    messageDigest.update(plainText.toByteArray())
    return BigInteger(messageDigest.digest()).toString(32)
}
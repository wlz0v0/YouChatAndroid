
package edu.buptsse.youchat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.buptsse.youchat.domain.User
import edu.buptsse.youchat.main.MainActivity
import edu.buptsse.youchat.main.curUser
import edu.buptsse.youchat.main.getFriendList
import edu.buptsse.youchat.main.msgMap
import edu.buptsse.youchat.ui.compose.*
import edu.buptsse.youchat.util.communication.*
import edu.buptsse.youxuancheng.ui.compose.*
import kotlinx.coroutines.*
import java.util.*

/**
 * 此Activity用于处理用户登录，以及可以跳转到注册和找回密码
 */
class LoginActivity : ComponentActivity(), CoroutineScope by MainScope() {
    companion object {
        /**
         * 登录状态，true为已登录
         */
        var loginState = false

        /**
         * @param id 登录的ID
         */
        suspend fun login(id: String,password:String): UserSystemMessage {
            var usm = UserSystemMessage()
            usm.id=id
            usm.password=password
            usm.type=UserSystemMessage.UserSystemMessageType.LOGIN
            sendChatByTCP(Message(id,id,SerializeUtil.object2Bytes(usm),Date(),Message.Type.USER_SYSTEM))
            usm = usmQueue.take()
            Log.d("usm.rS",usm.respondState.toString())
            return usm
        }
    }
    
    /**
     * 从LoginActivity跳转到RegisterActivity，注册账号
     */
    /*fun jumpFromLoginToRegister() {
        RegisterActivity.isRegister = true
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    */
    /**
     * 从LoginActivity跳转到RegisterActivity，找回密码
     *//*
    fun jumpFromLoginToFindPassword() {
        RegisterActivity.isRegister = false
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }*/

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // ActivityBody设置主题和颜色
            ActivityBody {
                Scaffold(
                    // 顶栏，返回键 + 登录标题
                    topBar = { TopBarWithTitleAndBack(title = "登录") { finish() } },
                    // 底栏，注册账号 + 找回密码
                    bottomBar = {
                        RegisterAndFind(this@LoginActivity)
                    }) {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 80.dp)) {
                        // 是否展示圆形进度条
                        var dialogIsShow by remember { mutableStateOf(false) }
                        // ID编辑框
                        var id by remember { mutableStateOf("") }
                        IDTextField(
                            id = id
                        ) { id = it }
                        // 密码编辑框
                        var password by remember { mutableStateOf("") }
                        PasswordTextField(
                            password = password,
                            placeholder = "请输入密码",
                        ) { password = it }
                        // 登录按钮
                        // 这个大小好看点
                        RoundedCornerButton(
                            text = "登录", modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            if (id == "") {
                                Toast.makeText(this@LoginActivity, "ID不能为空！", Toast.LENGTH_SHORT)
                                    .show()
                                return@RoundedCornerButton
                            }
                            if (password == "") {
                                Toast.makeText(this@LoginActivity, "密码不能为空！", Toast.LENGTH_SHORT)
                                    .show()
                                return@RoundedCornerButton
                            }
                            launch {
                                val deferred = async { return@async login(id, password) }
                                Log.d("!----------LOGIN---------deferred", deferred.await().toString())
                                val usm = deferred.await()
                                when (usm.respondState) {
                                    1 -> {
                                        Log.d("when","1")
                                        Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show()
                                        loginState = true
                                        curUser.id = id
                                        curUser.username=usm.name
                                        getFriendList()
                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                        // 登录成功结束本Activity
                                        finish()
                                    }
                                    -2 -> Toast.makeText(this@LoginActivity, "密码错误！", Toast.LENGTH_SHORT)
                                        .show()

                                    -1 -> Toast.makeText(this@LoginActivity, "ID不存在！", Toast.LENGTH_SHORT)
                                        .show()

                                    369 -> Toast.makeText(this@LoginActivity, "网络连接失败！", Toast.LENGTH_SHORT)
                                        .show()

                                    else -> {
                                        assert(false)
                                    }
                                }
                            }
                            // 完毕，关闭弹窗
                            dialogIsShow = false
                        }
                        // 开启弹窗
                        dialogIsShow = true
                    }
                    /*if (dialogIsShow) {
                            ProgressDialog("正在登录中···") { dialogIsShow = false }
                        }*/
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // 在LoginActivity生命周期结束时销毁所有协程
        cancel()
    }
}

/**
 * 将登录时间存入文件
 */
private suspend fun saveLoginDate() {
    withContext(Dispatchers.IO) {

    }
}

/**
 * 登录底部附加功能，注册新账号 + 找回密码
 */
@Composable
fun RegisterAndFind(fatherActivity: LoginActivity) {
    Box(
        modifier = Modifier
            .padding(bottom = 30.dp)
            .fillMaxWidth()
    ) {
        // 注册新账号按钮
        TextButton(
            onClick = { /*fatherActivity.jumpFromLoginToRegister()*/ },
            modifier = Modifier
                .align(Alignment.CenterStart)
                // 对称分布在左侧
                .padding(start = 50.dp)
        ) { BottomTextButtonText(text = "注册新账号") }
        // 这是一个竖直分割线，实在是找不到API了
        Text(
            text = "丨",
            fontSize = 26.sp,
            modifier = Modifier.align(Alignment.Center)
        )
        // 找回密码按钮
        TextButton(
            onClick = { /*fatherActivity.jumpFromLoginToFindPassword()*/ },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                // 对称分布在右侧
                .padding(end = 50.dp)
        ) { BottomTextButtonText(text = "找回密码") }
    }
}

/**
 * 底部附加功能文本, 16sp字体大小 + onBackground字体颜色
 *
 * @param text 文本内容
 */
@Composable
private fun BottomTextButtonText(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        color = MaterialTheme.colors.onBackground
    )
}
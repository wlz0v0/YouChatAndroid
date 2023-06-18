package edu.buptsse.youchat.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import edu.buptsse.youchat.LoginActivity
import edu.buptsse.youchat.Message
import edu.buptsse.youchat.util.communication.sendChatByTCP
import edu.buptsse.youchat.util.communication.transferInit
import kotlinx.coroutines.*
import java.util.*

class StartActivity : ComponentActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {}
        launch {
            withContext(Dispatchers.IO) {
                val res = async {
                    transferInit()
                    sendChatByTCP(Message("简自豪", "喻文波", "我才是第一ADC".toByteArray(), Date(), Message.Type.TEXT))
                }
                res.await()
                if (LoginActivity.loginState) {
                    startActivity(Intent(this@StartActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@StartActivity, LoginActivity::class.java))
                }
                finish()
            }
        }
    }
}
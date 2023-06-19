package edu.buptsse.youchat.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import edu.buptsse.youchat.LoginActivity
import edu.buptsse.youchat.service.CommunicationService
import edu.buptsse.youchat.util.communication.transferInit
import kotlinx.coroutines.*


class StartActivity : ComponentActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {}
        launch {
            withContext(Dispatchers.IO) {
                val res = async {
                    transferInit()
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

    override fun onResume() {
        super.onResume()
        startService(Intent(this, CommunicationService::class.java))
    }
}
package edu.buptsse.youchat.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.snapshots.SnapshotStateList
import edu.buptsse.youchat.LoginActivity
import edu.buptsse.youchat.service.CommunicationService
import edu.buptsse.youchat.util.communication.audioInit
import edu.buptsse.youchat.util.communication.transferInit
import kotlinx.coroutines.*


class StartActivity : ComponentActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {}
    }

    override fun onResume() {
        super.onResume()
        launch {
            withContext(Dispatchers.IO) {
                val res = async {
                    transferInit()
                    audioInit()
                }
                res.await()
                if (LoginActivity.loginState) {
                    startActivity(Intent(this@StartActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@StartActivity, LoginActivity::class.java))
                }
                friendList.forEach {
                    msgMap[it.id] = SnapshotStateList()
                }
                startService(Intent(this@StartActivity, CommunicationService::class.java))
                finish()
            }
        }
    }
}
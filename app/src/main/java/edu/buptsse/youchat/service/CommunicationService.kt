package edu.buptsse.youchat.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import edu.buptsse.youchat.chat.CallActivity
import edu.buptsse.youchat.util.communication.startReceiveMsg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CommunicationService : Service(), CoroutineScope by MainScope() {
    fun startCallActivity(friendId: String) {
        CallActivity.isReceiver.value = true
        CallActivity.friendId = friendId
        val intent = Intent(this, CallActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        launch {
            startReceiveMsg(this@CommunicationService)
        }
    }
}
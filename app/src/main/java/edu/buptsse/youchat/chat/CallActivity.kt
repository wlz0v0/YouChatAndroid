package edu.buptsse.youchat.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.buptsse.youchat.R
import edu.buptsse.youchat.main.getUserById
import edu.buptsse.youchat.ui.theme.Gray3
import kotlinx.coroutines.*

class CallActivity : ComponentActivity(), CoroutineScope by MainScope() {
    companion object {
        var friendId = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val friend = getUserById(friendId)
        val second = mutableStateOf(0)
        val minute = mutableStateOf(0)
        setContent {
            var isPickUp by remember { mutableStateOf(false) }
            Box(Modifier.fillMaxSize().background(Gray3)) {
                Column(Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
                    Text(
                        friend.username, Modifier.align(Alignment.CenterHorizontally).padding(bottom = 15.dp),
                        fontSize = 30.sp, fontWeight = FontWeight.Bold
                    )
                    val text = if (!isPickUp) {
                        "  等待对方接听..."
                    } else {
                        "已接通"
                    }
                    Text(text, Modifier.align(Alignment.CenterHorizontally), fontSize = 21.sp)
                }
                Column(Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(vertical = 20.dp)) {
                    Text(
                        "%02d".format(minute.value) + ":" + "%02d".format(second.value),
                        Modifier.align(Alignment.CenterHorizontally).padding(bottom = 10.dp),
                        fontSize = 20.sp
                    )
                    Button(
                        onClick = { finish() },
                        Modifier.align(Alignment.CenterHorizontally).size(120.dp, 75.dp).padding(vertical = 10.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(Color.Red, Color.White)
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_baseline_call_end_24), null,
                            Modifier.background(Color.Red).size(50.dp)
                        )
                    }
                }
            }
        }
        launch {
            var last: Long
            var cur: Long
            withContext(Dispatchers.Default) {
                last = System.currentTimeMillis()
                while (true) {
                    cur = System.currentTimeMillis()
                    if (cur - last > 1000) {
                        last = cur
                        launch {
                            second.value++
                            if (second.value == 60) {
                                second.value = 0
                                minute.value++
                            }
                        }
                    }
                }
            }
        }
    }
}
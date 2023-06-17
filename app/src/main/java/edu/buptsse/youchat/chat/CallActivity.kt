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
import androidx.compose.material.icons.Icons
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class CallActivity : ComponentActivity(), CoroutineScope by MainScope() {
    companion object {
        var friendId = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val friend = getUserById(friendId)
        setContent {
            Box(Modifier.fillMaxSize().background(Gray3)) {
                Column(Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
                    Text(
                        friend.username, Modifier.align(Alignment.CenterHorizontally).padding(bottom = 15.dp),
                        fontSize = 30.sp, fontWeight = FontWeight.Bold
                    )
                    Text("  等待对方接听...", Modifier.align(Alignment.CenterHorizontally), fontSize = 21.sp)
                }
                Column(Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(vertical = 20.dp)) {
                    Button(
                        onClick = {},
                        Modifier.align(Alignment.CenterHorizontally).size(120.dp, 60.dp),
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
    }
}
package edu.buptsse.youchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.buptsse.youchat.ui.theme.Gray3
import edu.buptsse.youchat.ui.theme.Teal200
import edu.buptsse.youchat.ui.theme.YouChatTheme
import kotlinx.coroutines.launch
import java.util.*

class ChatActivity : ComponentActivity() {
    companion object {
        var msg = SnapshotStateList<Pair<Boolean, Message>>()
        var name = "伍昶旭"
        var i = 0
    }

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YouChatTheme {
                val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
                BottomDrawer(
                    drawerContent = {
                        Column(Modifier.fillMaxWidth().height(200.dp)) {

                        }
                    },
                    drawerState = drawerState
                ) {
                    val chatTextFieldHeight = 60.dp

                    Box(Modifier.fillMaxHeight()) {
                        Column(
                            Modifier.padding(bottom = chatTextFieldHeight + 5.dp).fillMaxHeight().background(Gray3)
                        ) {
                            // 头部，聊天对象
                            Card(Modifier.fillMaxWidth().background(Teal200)) {
                                Box(Modifier.fillMaxWidth().background(Teal200)) {
                                    IconButton(
                                        { finish() },
                                        Modifier.align(Alignment.CenterStart).padding(start = 5.dp)
                                    ) {
                                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                                    }
                                    Text(
                                        name,
                                        Modifier.align(Alignment.Center),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = Color.White
                                    )
                                }
                            }
                            // 聊天内容
                            LazyColumn(Modifier.fillMaxSize()) {
                                msg.forEach { m ->
                                    item { MessageBox(m.first, m.second) }
                                }
                            }
                        }
                        // 编辑框
                        Box(
                            Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                                .height(chatTextFieldHeight)
                        ) {
                            var chatText by remember { mutableStateOf("") }
                            var isSendButton by remember { mutableStateOf(false) }
                            OutlinedTextField(
                                value = chatText,
                                onValueChange = {
                                    chatText = it
                                    isSendButton = chatText.isNotEmpty()
                                },
                                modifier = Modifier.padding(5.dp, 5.dp, 100.dp, 5.dp).fillMaxWidth(),
                                /*leadingIcon = { Icon(painterResource(R.drawable.ic_baseline_search_24), contentDescription = null) },
                            trailingIcon = {
                                Button(
                                    onClick = { onClick() },
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.padding(top = 3.dp, bottom = 3.dp, end = 2.dp).width(80.dp)
                                ) { Text("搜索", fontSize = 15.sp, modifier = Modifier.align(Alignment.CenterVertically)) }
                            },*/
                                shape = RoundedCornerShape(50),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    backgroundColor = MaterialTheme.colors.background,
                                    focusedBorderColor = MaterialTheme.colors.background,
                                    unfocusedBorderColor = MaterialTheme.colors.background,
                                    textColor = Color.Black,
                                )
                            )
                            if (isSendButton) {
                                Button(
                                    onClick = {
                                        val message =
                                            Message("wlz", "wcx", chatText.toByteArray(), Date(), Message.Type.TEXT)
                                        msg.add(Pair(i % 2 == 0, message))
                                        chatText = ""
                                        isSendButton = false
                                    },
                                    modifier = Modifier.padding(10.dp, 5.dp).align(Alignment.CenterEnd).width(80.dp),
                                    // 是否能被点击
                                    enabled = true,
                                    // 来个圆角按钮
                                    shape = RoundedCornerShape(50),
                                    // 按钮背景颜色和主题背景颜色区分开，所以使用secondary
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.secondary,
                                        contentColor = MaterialTheme.colors.onBackground
                                    )
                                ) {
                                    Text(text = "发送", color = Color.White, fontSize = 20.sp)
                                }
                            } else {
                                val scope = rememberCoroutineScope()
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            drawerState.expand()
                                        }
                                    },
                                    modifier = Modifier.padding(5.dp, 5.dp).align(Alignment.CenterEnd)
                                        .clip(CircleShape),
                                    shape = CircleShape,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        backgroundColor = Color.White,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Icon(Icons.Default.Add, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBox(isSend: Boolean, message: Message) {
    val bkgColor = if (isSend) Color.Blue else Color.White
    val textColor = if (isSend) Color.White else Color.Black
    val align = if (isSend) Alignment.End else Alignment.Start
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 8.dp)) {
        Card(Modifier.background(Gray3).align(align), RoundedCornerShape(10)) {
            Text(
                message.text,
                Modifier.background(bkgColor).align(Alignment.CenterHorizontally).padding(5.dp),
                color = textColor,
                fontSize = 22.sp
            )
        }
    }
}
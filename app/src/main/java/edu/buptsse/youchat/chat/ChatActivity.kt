@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package edu.buptsse.youchat.chat

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.buptsse.youchat.Message
import edu.buptsse.youchat.R
import edu.buptsse.youchat.domain.User
import edu.buptsse.youchat.main.curUser
import edu.buptsse.youchat.main.msgMap
import edu.buptsse.youchat.ui.theme.Gray3
import edu.buptsse.youchat.ui.theme.Teal200
import edu.buptsse.youchat.ui.theme.YouChatTheme
import edu.buptsse.youchat.util.FileUri
import edu.buptsse.youchat.util.communication.CALL_REQUEST
import edu.buptsse.youchat.util.communication.audioInit
import edu.buptsse.youchat.util.communication.sendCallReq
import edu.buptsse.youchat.util.communication.sendMessage
import edu.buptsse.youchat.util.getBitmapFromUri
import edu.buptsse.youchat.util.hasMicrophonePermission
import edu.buptsse.youchat.util.hasReadAndWriteStoragePermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*

class ChatActivity : ComponentActivity(), CoroutineScope by MainScope() {
    companion object {
        var msg = SnapshotStateList<Pair<Boolean, Message>>()
        var friend = User("10002", "伍昶旭", "")
        var i = 0
    }

    private lateinit var imageUri: Uri
    private lateinit var fileUri: Uri
 
    private val readPermission = Manifest.permission.READ_EXTERNAL_STORAGE
    private val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val audioPermission = Manifest.permission.RECORD_AUDIO

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it[readPermission]!! && it[writePermission]!!) {
            Log.i("ok", "ok ok")
        } else {
            Toast.makeText(
                this@ChatActivity, "申请权限失败", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            Log.i("ok", "ok ok")
        } else {
            Toast.makeText(
                this@ChatActivity, "申请权限失败", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val maxHeight = 400
    private val maxWidth = 200
    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            val bitmap = getBitmapFromUri(maxHeight, maxWidth, uri)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val message = Message("武连增", friend.username, outputStream.toByteArray(), Date(), Message.Type.IMAGE)
            msg.add(Pair(true, message))
            msgMap[friend.id] = msg
        }
    }

    private val getFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            fileUri = uri
            Log.e("file uri", uri.toString())
            val path = FileUri.getFilePathByUri(this, uri)
            path?.let { Log.e("file path", it) }
        }
    }

    private fun jumpFromChatToCall(friendId: String) {
        CallActivity.friendId = friendId
        startActivity(Intent(this, CallActivity::class.java))
    }

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YouChatTheme {
                val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
                BottomDrawer(
                    drawerContent = {
                        Box(Modifier.fillMaxWidth().height(100.dp)) {
                            Row(Modifier.fillMaxWidth().align(Alignment.Center)) {
                                val scope = rememberCoroutineScope()
                                IconTextButton(R.drawable.ic_baseline_image_24, "图片") {
                                    // on click
                                    if (!hasReadAndWriteStoragePermission()) {
                                        requestPermissions.launch(arrayOf(readPermission, writePermission))
                                    } else {
                                        getImage.launch("image/*")
                                    }
                                    scope.launch { drawerState.close() }
                                }
                                IconTextButton(R.drawable.ic_baseline_insert_drive_file_24, "文件") {
                                    // on click
                                    if (!hasReadAndWriteStoragePermission()) {
                                        requestPermissions.launch(arrayOf(readPermission, writePermission))
                                    } else {
                                        getFile.launch("application/pdf")
                                    }
                                    scope.launch { drawerState.close() }
                                }
                                IconTextButton(R.drawable.ic_baseline_call_24, "语音") {
                                    // on click
                                    if (!hasMicrophonePermission()) {
                                        requestPermission.launch(audioPermission)
                                    } else {
                                        audioInit()
                                        launch {
                                            sendCallReq(friend.id, CALL_REQUEST)
                                        }
                                        jumpFromChatToCall(friend.id)
                                    }
                                    scope.launch { drawerState.close() }
                                }
                            }
                        }
                    }, drawerState = drawerState
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
                                        { finish() }, Modifier.align(Alignment.CenterStart).padding(start = 5.dp)
                                    ) {
                                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                                    }
                                    Text(
                                        friend.username,
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
                            Modifier.fillMaxWidth().align(Alignment.BottomCenter).height(chatTextFieldHeight)
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
                                        val message = Message(
                                            curUser.id, friend.id,
                                            chatText.toByteArray(),
                                            Date(), Message.Type.TEXT
                                        )
                                        msg.add(Pair(true, message))
                                        launch {
                                            sendMessage(message)
                                        }
                                        chatText = ""
                                        isSendButton = false
                                    }, modifier = Modifier.padding(10.dp, 5.dp).align(Alignment.CenterEnd).width(80.dp),
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
                                        backgroundColor = Color.White, contentColor = Color.Black
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
            when (message.dataType) {
                Message.Type.TEXT -> {
                    Text(
                        message.text,
                        Modifier.background(bkgColor).align(Alignment.CenterHorizontally).padding(5.dp),
                        color = textColor,
                        fontSize = 22.sp
                    )
                }

                Message.Type.IMAGE -> {
                    val bitmap = BitmapFactory.decodeByteArray(message.data, 0, message.data.size)
                    Image(bitmap.asImageBitmap(), null)
                }

                else -> throw RuntimeException()
            }
        }
    }

}

@Composable
private fun RowScope.IconTextButton(id: Int, text: String, onClick: () -> Unit) {
    Box(Modifier.weight(1.0f).height(70.dp)) {
        OutlinedButton(onClick = { onClick() }, modifier = Modifier.align(Alignment.TopCenter)) {
            Icon(painterResource(id), null, tint = Color.Black)
        }
        Text(text, fontSize = 17.sp, modifier = Modifier.align(Alignment.BottomCenter), fontWeight = FontWeight.Bold)
    }
}
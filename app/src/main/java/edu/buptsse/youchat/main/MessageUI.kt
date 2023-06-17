package edu.buptsse.youchat.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.buptsse.youchat.Message
import edu.buptsse.youchat.domain.User
import edu.buptsse.youchat.ui.theme.Gray5
import edu.buptsse.youchat.ui.theme.Teal200
import java.text.SimpleDateFormat
import java.util.*

/**
 * key是好友的id，value是和好友的聊天消息
 */
val msgMap = mutableStateMapOf(
    "10002" to mutableStateListOf<Pair<Boolean, Message>>(
        Pair(
            false, Message(
                curUser.id, "10002",
                "This is a test".toByteArray(), Date(), Message.Type.TEXT
            )
        )
    ),
    "10003" to mutableStateListOf<Pair<Boolean, Message>>(
        Pair(
            false, Message(
                curUser.id, "10003",
                "This is a test".toByteArray(), Date(), Message.Type.TEXT
            )
        )
    )
)

@Composable
fun MessageUI(activity: MainActivity) {
    Column(Modifier.background(Color.White).fillMaxWidth()) {
        Column(Modifier.background(Teal200).fillMaxWidth()) {
            Text(
                "疼逊扣扣",
                Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // TODO: bottom padding
        LazyColumn(Modifier.fillMaxWidth().padding(bottom = 0.dp)) {
            msgMap.entries.forEach {
                item { MessageListView(getUserById(it.key), activity) }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MessageListView(friend: User, activity: MainActivity) {
    val msgList = msgMap[friend.id]!!
    ListItem(
        Modifier.fillMaxWidth().clickable {
            activity.jumpFromMainToChat(friend.id)
        },
        secondaryText = {
            if (msgList.size != 0) {
                val msg = msgList.last().second
                val text = when (msg.dataType) {
                    Message.Type.TEXT -> msg.text
                    Message.Type.IMAGE -> "[图片]"
                    Message.Type.FILE -> "[文件]"
                    else -> ""
                }
                Text(text, color = Gray5, fontSize = 19.sp)
            }
        }
    ) {
        Box {
            // 昵称
            Text(friend.username, Modifier.align(Alignment.CenterStart), fontSize = 21.sp, fontWeight = FontWeight.Bold)
            // 时间
            if (msgList.size != 0) {
                val date = msgList.last().second.time
                val format = SimpleDateFormat("yyyy-MM-dd aa hh:mm", Locale.CHINESE)
                Text(format.format(date), Modifier.align(Alignment.CenterEnd), color = Gray5, fontSize = 18.sp)
            }
        }
    }
    Divider(thickness = 2.dp)
}

package edu.buptsse.youchat.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.buptsse.youchat.Message
import edu.buptsse.youchat.domain.User
import edu.buptsse.youchat.ui.theme.Gray3
import edu.buptsse.youchat.ui.theme.Gray5
import edu.buptsse.youchat.ui.theme.Teal200
import edu.buptsse.youxuancheng.network.getUser
import java.util.*

/**
 * key是好友的id，value是和好友的聊天消息
 */
val msgMap = mutableStateMapOf(
    "10002" to mutableStateListOf<Pair<Boolean, Message>>(Pair(false, Message("武连增", "伍昶旭",
        "This is a test".toByteArray(), Date(), Message.Type.TEXT)))
)

@Composable
fun MessageUI(activity: MainActivity) {
    Column(Modifier.background(Color.White).fillMaxWidth()) {
        Column(Modifier.background(Teal200).fillMaxWidth()) {
            Card(Modifier.align(Alignment.CenterHorizontally)) {
                Text(
                    "疼逊扣扣",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(Modifier.fillMaxWidth().padding(bottom = 50.dp)) {
            msgMap.entries.forEach {
                item { MessageListView(getUser(it.key), activity) }
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
                Text(msgList.last().second.text, color = Gray5, fontSize = 19.sp)
            }
        }) {
        Text(friend.username, fontSize = 21.sp, fontWeight = FontWeight.Bold)
    }
    Divider(thickness = 2.dp)
}

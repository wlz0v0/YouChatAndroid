package edu.buptsse.youchat.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.buptsse.youchat.Message
import edu.buptsse.youchat.UserSystemMessage
import edu.buptsse.youchat.domain.User
import edu.buptsse.youchat.ui.theme.Gray5
import edu.buptsse.youchat.ui.theme.Teal200
import edu.buptsse.youchat.util.communication.SerializeUtil
import edu.buptsse.youchat.util.communication.sendChatByTCP
import edu.buptsse.youchat.util.communication.usmQueue
import kotlinx.coroutines.async
import java.util.*

/**
 * key是好友的id，value是和好友的聊天消息
 */
val friendList = mutableStateListOf<User>()

suspend fun getFriendList() {
    Log.d("getFriendList", "start")

    var usm: UserSystemMessage = UserSystemMessage()
    usm.id = curUser.id
    usm.type = UserSystemMessage.UserSystemMessageType.GET_FRIEND_LIST
    val msg = Message(curUser.id, curUser.id, SerializeUtil.object2Bytes(usm), Date(), Message.Type.USER_SYSTEM)
    sendChatByTCP(msg)
    Log.d("getFriendList", "waiting")
    usm = usmQueue.take()
    Log.d("getFriendList", "waited")
    for (friend in usm.friendList) {
        friendList.add(User(friend.id, friend.name, ""))
        msgMap[friend.id] = mutableStateListOf<Pair<Boolean, Message>>(
//            Pair(
//                false, Message(
//                    curUser.id, friend.id,
//                    "This is a test".toByteArray(), Date(), Message.Type.TEXT
//                )
//            )
        )
    }
}

fun getUserById(id: String): User {
    return friendList.stream().filter { it.id == id }.findFirst().get()
}


@Composable
fun FriendUI(activity: MainActivity) {
    Column(Modifier.background(Color.White).fillMaxWidth()) {
        Column(Modifier.background(Teal200).fillMaxWidth()) {
            Text(
                "好友列表",
                Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // TODO: bottom padding
        LazyColumn(Modifier.fillMaxWidth().padding(bottom = 0.dp)) {
            item { FriendListView(activity) }
        }
        Row {
            TextButton(
                onClick = {

                },
//                Modifier.align(Alignment.Start).padding(top = 15.dp)
            ) {
                Text("添加好友", color = Color.Black, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(
                onClick = {  },
//                Modifier.align(Alignment.End).padding(top = 15.dp)
            ) {
                Text("新的好友", color = Color.Black, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FriendListView(activity: MainActivity) {
    for (friend in friendList) {
        ListItem(
            Modifier.fillMaxWidth().clickable {
                activity.jumpFromMainToChat(friend.id)
            },
        ) {
            Box(Modifier.fillMaxWidth()) {
                // 昵称
                Text(
                    friend.username,
                    Modifier.align(Alignment.CenterStart),
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
                // id
                Text(friend.id, Modifier.align(Alignment.CenterEnd), color = Gray5, fontSize = 18.sp)
            }
        }
        Divider(thickness = 2.dp)
    }
}
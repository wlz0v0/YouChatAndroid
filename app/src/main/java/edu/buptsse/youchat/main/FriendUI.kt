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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.buptsse.youchat.domain.User
import edu.buptsse.youchat.ui.theme.Gray5
import edu.buptsse.youchat.ui.theme.Teal200
import edu.buptsse.youchat.util.UserConfig

/**
 * key是好友的id，value是和好友的聊天消息
 */
val friendList = mutableStateListOf<User>(
    UserConfig.wcx,
    UserConfig.dhn
)

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
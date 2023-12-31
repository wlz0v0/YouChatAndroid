@file:OptIn(ExperimentalMaterialApi::class)

package edu.buptsse.youchat.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.buptsse.youchat.LoginActivity
import edu.buptsse.youchat.R
import edu.buptsse.youchat.ui.theme.Gray5
import edu.buptsse.youchat.ui.theme.Teal200
import edu.buptsse.youchat.util.UserConfig
import java.util.regex.Pattern

var curUser: edu.buptsse.youchat.domain.User = UserConfig.nobody
var token = ""
val passwordPattern: Pattern = Pattern.compile("")
val phoneNumberPattern: Pattern = Pattern.compile("")

@Composable
fun AccountUI(activity: MainActivity, navController: NavController) {
    Box(Modifier.padding(bottom = 60.dp).fillMaxHeight()) {
        Column(Modifier.fillMaxWidth()) {
            Column(Modifier.background(Teal200).fillMaxWidth()) {
                Text(
                    "我的信息",
                    Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp),
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Card(Modifier.fillMaxWidth().height(100.dp), backgroundColor = Color.White) {
                Box(Modifier.padding(10.dp).background(Color.White)) {
                    Column(
                        Modifier.align(Alignment.CenterStart).clickable {
                            if (curUser == UserConfig.nobody) {
                                activity.jumpFromMainToLogin()
                            }
                        }
                    ) {
                        Text(
                            curUser.username,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                        )
                        Text(
                            "ID: ${curUser.id}",
                            fontSize = 19.sp,
                            modifier = Modifier.padding(start = 10.dp, top = 5.dp),
                            color = Gray5
                        )
                    }
                    IconButton(onClick = {}, Modifier.align(Alignment.CenterEnd)) {
                        Icon(painterResource(R.drawable.ic_baseline_exit_to_app_24), null)
                    }
                }
            }
        }
        TextButton(
            onClick = { LoginActivity.loginState = false },
            Modifier.align(Alignment.BottomCenter).padding(top = 15.dp)
        ) {
            Text("退出登录", color = Color.Red, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        }
    }
}
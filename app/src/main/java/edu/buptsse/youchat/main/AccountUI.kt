package edu.buptsse.youchat.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.buptsse.youchat.ui.theme.Gray5
import edu.buptsse.youchat.util.UserConfig
import java.util.regex.Pattern

var curUser = UserConfig.wcx
var token = ""
val passwordPattern: Pattern = Pattern.compile("")
val phoneNumberPattern: Pattern = Pattern.compile("")

@Composable
fun AccountUI(activity: MainActivity, navController: NavController) {
    Card(Modifier.fillMaxWidth().height(100.dp), backgroundColor = Color.White) {
        Box(Modifier.padding(10.dp).background(Color.White)) {
            Column(Modifier.align(Alignment.CenterStart)) {
//                Icon(Icons.Default.AccountBox, null, modifier = Modifier.size(75.dp))
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
        }
    }
}
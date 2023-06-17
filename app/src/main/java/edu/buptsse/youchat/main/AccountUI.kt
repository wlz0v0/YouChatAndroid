package edu.buptsse.youchat.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import edu.buptsse.youchat.domain.User
import java.util.regex.Pattern

var curUser = User("10001", "武连增", "123456")
var token = ""
val passwordPattern: Pattern = Pattern.compile("")
val phoneNumberPattern: Pattern = Pattern.compile("")

@Composable
fun AccountUI(activity: MainActivity, navController: NavController) {

}
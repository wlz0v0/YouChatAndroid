package edu.buptsse.youchat.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.buptsse.youchat.LoginActivity
import edu.buptsse.youchat.chat.ChatActivity
import edu.buptsse.youchat.ui.theme.YouChatTheme

class MainActivity : ComponentActivity() {

    fun jumpFromMainToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun jumpFromMainToChat(id: String) {
        ChatActivity.msg = msgMap[id]!!
        ChatActivity.friend = getUserById(id)
        startActivity(Intent(this, ChatActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YouChatTheme {
                // A surface container using the 'background' color from the theme
                val navControllers = rememberNavController()
                Scaffold(
                    topBar = {

                    },
                    bottomBar = {
                        var selectedItem by remember { mutableStateOf(0) }
                        val items = listOf(
                            MainBottomNavItem.Message,
                            MainBottomNavItem.Friend,
                            MainBottomNavItem.Account
                        )
                        BottomAppBar(cutoutShape = RoundedCornerShape(80.dp)) {
                            items.forEachIndexed { index, it ->
                                BottomNavigationItem(
                                    icon = {
                                        Icon(
                                            painterResource(it.resId),
                                            contentDescription = null
                                        )
                                    },
                                    modifier = Modifier.padding(
                                        start = it.startPadding,
                                        end = it.endPadding
                                    ),
                                    label = { Text(it.route) },
                                    selected = selectedItem == index,
                                    onClick = {
                                        if (!LoginActivity.loginState && index == 2) {
                                            jumpFromMainToLogin()
                                        } else {
                                            selectedItem = index
                                            navControllers.navigate(it.route) {
                                                // 跳转到首页
                                                popUpTo(navControllers.graph.startDestinationId)
                                                // 一次只能展示一个页面
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) {
                    NavHost(
                        navControllers,
                        startDestination = MainBottomNavItem.Message.route
                    ) {
                        composable(MainBottomNavItem.Message.route) { MessageUI(this@MainActivity) }
                        composable(MainBottomNavItem.Friend.route) { FriendUI(this@MainActivity) }
                        composable(MainBottomNavItem.Account.route) { AccountUI(this@MainActivity, navControllers) }
                    }
                }
            }
        }
    }
}
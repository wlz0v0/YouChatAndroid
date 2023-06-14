package edu.buptsse.youchat.ui.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import edu.buptsse.youchat.ui.theme.YouChatTheme

/**
 * 根据当前主题设置颜色
 */
@Composable
inline fun ActivityBody(crossinline content: @Composable () -> Unit) {
    // 获取当前主题
    val darkTheme = isInDarkTheme()
    YouChatTheme (darkTheme = darkTheme) {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}
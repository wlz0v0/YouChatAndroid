package edu.buptsse.youchat.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * 带有圆形进度条的Dialog，进度条下方是提示文字
 *
 * @param text 提示文本
 * @param onDismissRequest 关闭回调
 * @receiver
 */
@Composable
fun ProgressDialog(text: String, onDismissRequest: () -> Unit) {
    // 进度条和文本水平居中放置
    val ch = Alignment.CenterHorizontally
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Dialog背景透明，使用Card添加一个不透明的背景
        Card(modifier = Modifier.size(width = 300.dp, height = 150.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 40.dp)) {
                // 圆形进度条
                CircularProgressIndicator(modifier = Modifier.align(ch))
                // 提示文本
                Text(text, modifier = Modifier.padding(top = 20.dp).align(ch))
            }
        }
    }
}
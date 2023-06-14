package edu.buptsse.youxuancheng.ui.compose

import androidx.activity.ComponentActivity
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

/**
 * 更新弹窗
 */
@Composable
fun UpdateDialog(fatherActivity: ComponentActivity, onDismiss: () -> Unit) {
    AlertDialog(
        title = { Text("存在最新版本，是否更新？", color = MaterialTheme.colors.onBackground) },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
//                downloadApk(fatherActivity)
                onDismiss()
            }) { Text("更新", color = MaterialTheme.colors.onBackground) }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    "取消",
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    )
}
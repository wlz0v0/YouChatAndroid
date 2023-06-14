package edu.buptsse.youchat.ui.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 除了MainActivity之外的TopAppBar，包括一个标题和一个返回键，标题居中
 *
 * @param title TopAppBar的标题名
 * @param onClick 返回键点击回调
 */
@Composable
fun TopBarWithTitleAndBack(title: String = "", onClick: () -> Unit) {
    TopAppBar(
        title = {
            TopBarText(title = title)
        },
        navigationIcon = {
            IconButton(onClick = { onClick() }) {
                Icon(
                    // 图标样式为向左指的箭头 ←
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    )
}

/**
 * TopAppBar标题文本，标题居中
 *
 * @param title 标题名
 * @param isMain 是否为MainActivity
 */
@Composable
fun TopBarText(title: String = "发票识别", isMain: Boolean = false) {
    // MainActivity右侧有一个Action左侧无Navigation
    // 其他Activity右侧无Action左侧有Navigation
    // 根据测量得到这两组数据可以使标题居中
    val start = if (isMain) 48.dp else 0.dp
    val end = if (isMain) 12.dp else 68.dp
    Text(
        text = title,
        // 文本居中
        textAlign = TextAlign.Center,
        // 文本框铺满空间
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = start, end = end)
    )
}
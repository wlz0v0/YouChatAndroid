/**
 * 北京邮电大学创新创业训练项目——出租车发票识别
 *
 * author 武连增
 *
 * e-mail: wulianzeng@bupt.edu.cn
 */
package edu.buptsse.youchat.main

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import edu.buptsse.youchat.R

/**
 * 定义MainActivity底部导航栏每个Item的属性
 *
 * @param route 导航字符串，也是展示的文本
 * @param resId 图片对应资源文件的ID
 * @param startPadding 图片和文字距离start的距离
 * @param endPadding 图片和文字距离end的距离
 */
sealed class MainBottomNavItem(
    var route: String,
    var resId: Int,
    var startPadding: Dp,
    var endPadding: Dp
) {
    object Message : MainBottomNavItem("消息", R.drawable.ic_baseline_message_24, 0.dp, 30.dp)
    object Friend : MainBottomNavItem("好友", R.drawable.ic_baseline_contacts_24, 15.dp, 15.dp)
    object Account : MainBottomNavItem("我的", R.drawable.ic_baseline_person_24, 30.dp, 0.dp)
}
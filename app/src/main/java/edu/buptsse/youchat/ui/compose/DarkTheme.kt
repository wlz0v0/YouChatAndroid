package edu.buptsse.youchat.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableStateOf

/**
 * 当前是否是夜间模式，默认白天模式：
 * true-夜间模式，false-白天模式
 */
private var darkTheme = mutableStateOf(false)

/**
 * 判断当前是否是夜间模式
 *
 * @return 当前是否处于夜间模式
 */
@Composable
@ReadOnlyComposable
fun isInDarkTheme() = darkTheme.value

/**
 * 白天模式和夜间模式之间切换
 */
fun changeTheme() {
    darkTheme.value = darkTheme.value.not()
}
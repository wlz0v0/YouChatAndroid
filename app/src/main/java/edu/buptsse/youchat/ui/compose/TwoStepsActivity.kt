package edu.buptsse.youxuancheng.ui.compose

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import edu.buptsse.youchat.ui.compose.ActivityBody
import edu.buptsse.youchat.ui.compose.TopBarWithTitleAndBack

/**
 * 本Activity适用于有两步操作的Activity
 */
abstract class TwoStepsActivity : ComponentActivity() {
    /**
     * 注册一共有两个页面，一个是输入手机号和验证码，
     * 另一个是输入密码和重复密码，当为true时在第一个页面，
     * false时在第二个页面
     */
    protected var isFirstButton = mutableStateOf(true)

    @OptIn(ExperimentalAnimationApi::class)
    protected lateinit var navController: NavHostController

    open var title = ""

    open var naviButtonBottomPadding = 100.dp

    /**
     * 第一步操作的界面
     */
    @Composable
    abstract fun naviItem1()

    /**
     * 第二步操作的界面
     */
    @Composable
    abstract fun naviItem2()

    /**
     * 补充界面
     */
    @Composable
    abstract fun content()

    /**
     * 为了处理点击底部返回键覆写一下
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // 当用户点击底部返回按钮时
        // 如果在第二个页面，即isFirstButton.value为false时
        // 修改为true，剩下的交给super
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isFirstButton.value) {
                isFirstButton.value = true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ActivityBody {
                // 当前是否位于第一个页面
                var isFirstButton by remember { isFirstButton }
                navController = rememberAnimatedNavController()

                Scaffold(topBar = {
                    TopBarWithTitleAndBack(title) {
                        // 当位于第一个页面时，结束Activity
                        if (isFirstButton) {
                            finish()
                        } else {
                            // 当位于第二个页面时，跳转到第一个页面
                            isFirstButton = true
                            navController.popBackStack()
                        }
                    }
                }) {
                    Box(modifier = Modifier.fillMaxHeight()) {
                        AnimatedNavHost(
                            navController = navController,
                            startDestination = "1",
                            modifier = Modifier.align(Alignment.TopCenter),
                            // 进入动画，从屏幕右边加载到中间
                            enterTransition = { slideInHorizontally { it } },
                            // 弹出时上一个页面的进入动画，从屏幕左边加载到中间
                            popEnterTransition = { slideInHorizontally { -it } },
                            // 离开动画，从中间移动到屏幕左边
                            exitTransition = { slideOutHorizontally { -it } },
                            // 弹出时本页面离开动画，从中间移动到屏幕右边
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            composable("1") {
                                naviItem1()
                            }
                            composable("2") {
                                naviItem2()
                            }
                        }
                        // 底部导航按钮，只是表明现在在第几步
                        NavigationButtons(
                            isFirstButton = isFirstButton,
                            modifier = Modifier.align(Alignment.BottomCenter),
                            bottomPadding = naviButtonBottomPadding
                        )
                        content()
                    }
                }
            }
        }
    }
}

/**
 * 底部两个导航按钮
 *
 * @param isFirstButton 当前是否是第一个按钮被选中
 * @param modifier 让按钮在底部居中的位置
 */
@Composable
private fun NavigationButtons(isFirstButton: Boolean, modifier: Modifier, bottomPadding: Dp) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = bottomPadding)
    ) {
        // 按钮1，表明正处于第一步
        NavigationButton(
            isFirstButton = isFirstButton,
            // 写个1上去
            text = "1",
            // 分布在左侧
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 80.dp)
        )
        // 按钮2，表明正处于第二步
        NavigationButton(
            isFirstButton = !isFirstButton,
            // 写个2上去
            text = "2",
            // 分布在右侧
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 80.dp)
        )
    }
}

/**
 * 单个导航按钮
 *
 * @param isFirstButton 当前是否是第一个按钮被选中
 * @param text 按钮文本
 * @param modifier 把按钮放在合适的位置上
 */
@Composable
private fun NavigationButton(isFirstButton: Boolean, text: String, modifier: Modifier) {
    val pressedColors =
        ButtonDefaults.buttonColors(disabledBackgroundColor = MaterialTheme.colors.primary)
    val notPressedColors = ButtonDefaults.buttonColors()
    Button(
        // 由于按钮不可点击所以回调为空
        onClick = { },
        // 按钮不可点击
        enabled = false,
        // 分布好看点
        modifier = modifier,
        // 圆形按钮
        shape = RoundedCornerShape(100),
        // 根据是否被选择来选择颜色
        colors = if (isFirstButton)
            pressedColors
        else notPressedColors
    ) {
        Text(text = text, fontSize = 22.sp)
    }
}

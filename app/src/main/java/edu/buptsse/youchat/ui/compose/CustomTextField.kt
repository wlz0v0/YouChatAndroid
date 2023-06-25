package edu.buptsse.youchat.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.buptsse.youchat.R
import edu.buptsse.youchat.ui.theme.Gray5

/**
 * 输入账号的TextField
 *
 * @param phoneNumber 编辑框修改的文本
 * @param onValueChange 当输入服务更新文本时触发的回调，更新的文本作为回调的参数出现
 */
@Composable
fun ColumnScope.PhoneNumberTextField(
    phoneNumber: String,
    onValueChange: (String) -> Unit,
) {
    TextField(
        value = phoneNumber,
        onValueChange = { onValueChange(it) },
        // TextField设置居中
        modifier = Modifier.align(Alignment.CenterHorizontally).semantics { error("test") },
        // 方形账户图案
        leadingIcon = {
            Icon(Icons.Filled.AccountBox, contentDescription = null)
        },
        // 一行内展示
        singleLine = true,
        // 无输入时的提示文本
        placeholder = { Text("请输入手机号", color = MaterialTheme.colors.onBackground) },
        // 输入类型设为数字
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        // 背景色设为白色
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.background,
            textColor = MaterialTheme.colors.onBackground
        )
    )
}

/**
 * 输入密码的TextField
 *
 * @param password 编辑框修改的文本
 * @param placeholder 没有文本时显示的提示
 * @param onValueChange 当输入服务更新文本时触发的回调，更新的文本作为回调的参数出现
 */
@Composable
fun ColumnScope.PasswordTextField(
    password: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    TextField(
        value = password,
        // 为了代码复用，必须把password传进来，又因为函数式编程，还得定义一个回调在外面修改ToT
        onValueChange = { onValueChange(it) },
        // TextField设置居中
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 15.dp),
        // 锁的图案，表示密码
        leadingIcon = {
            Icon(Icons.Filled.Lock, contentDescription = null)
        },
        // 控制密码是否隐藏
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                // 根据当前密码是否隐藏选择图片
                val resId =
                    if (passwordHidden) R.drawable.ic_baseline_visibility_24 else R.drawable.ic_baseline_visibility_off_24
                // 根据当前密码是否隐藏选择提示
                val description = if (passwordHidden) "展示密码" else "隐藏密码"
                Icon(
                    painterResource(id = resId),
                    contentDescription = description
                )
            }
        },
        // 一行内展示
        singleLine = true,
        // 无输入时的提示文本
        placeholder = { Text(placeholder, color = MaterialTheme.colors.onBackground) },
        // 密码是否隐藏
        visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
        // 输入类型设为密码
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        // 背景色设为白色
        colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.background)
    )
}

@Composable
fun ColumnScope.PasswordInstruction() {
    val ch = Alignment.CenterHorizontally
    Column(Modifier.align(ch).padding(end = 25.dp)) {
        Text("密码6~16位", fontSize = 14.sp, color = Gray5, modifier = Modifier.padding(bottom = 5.dp))
        Text("可以包含大写字母、小写字母、数字", fontSize = 14.sp, color = Gray5)
    }
}

@Composable
fun ColumnScope.RePasswordInstruction() {
    val ch = Alignment.CenterHorizontally
    Column(Modifier.align(ch).padding(end = 120.dp)) {
        Text("重复输入与密码相同", fontSize = 14.sp, color = Gray5, modifier = Modifier.padding(bottom = 5.dp))
    }
}

@Composable
fun ColumnScope.NameTextField(name: String, onValueChange: (String) -> Unit) {
    TextField(
        value = name,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.align(Alignment.CenterHorizontally),
        leadingIcon = {
            Icon(
                painterResource(R.drawable.ic_baseline_person_24),
                contentDescription = null
            )
        },
        placeholder = { Text("请输入名字", color = MaterialTheme.colors.onBackground) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        // 背景色设为白色
        colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.background)
    )
}


@Composable
fun ColumnScope.IDTextField(id: String, onValueChange: (String) -> Unit) {
    TextField(
        value = id,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.align(Alignment.CenterHorizontally),
        leadingIcon = {
            Icon(
                painterResource(R.drawable.ic_baseline_person_24),
                contentDescription = null
            )
        },
        placeholder = { Text("请输入ID", color = MaterialTheme.colors.onBackground) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        // 背景色设为白色
        colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.background)
    )
}

/*
@Composable
fun ColumnScope.EmailTextField(email: String, onValueChange: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.align(Alignment.CenterHorizontally),
        leadingIcon = {
            Icon(
                painterResource(R.drawable.ic_baseline_email_24),
                contentDescription = null
            )
        },
        singleLine = true,
        placeholder = { Text("请输入邮箱", color = MaterialTheme.colors.onBackground) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        // 背景色设为白色
        colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.background)
    )
}*/

package com.yaduo.commontest.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yaduo.common.log.LogUtil

/** ### 日志测试页面 */
@Composable
fun LogScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TestButton("打印 DEBUG") {
            LogUtil.d(content = "这是一条 Debug 级别的测试日志")
        }
        TestButton("打印 INFO") {
            LogUtil.i(content = "这是一条 Info 级别的测试日志")
        }
        TestButton("打印 WARN") {
            LogUtil.w(content = "这是一条 Warn 级别的测试日志")
        }
        TestButton("打印 ERROR (带 Throwable)") {
            LogUtil.e(
                content = "这是一条带 Exception 的 Error 日志",
                throwable = RuntimeException("测试异常信息")
            )
        }
    }
}

@Composable
fun TestButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.fillMaxWidth()
    ) { Text(text) }
}

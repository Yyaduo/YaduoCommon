package com.yaduo.commontest.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yaduo.common.extra.showToast

/** ### 扩展工具测试页面 */
@Composable
fun ExtraScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = { context.showToast("这是一条来自 Context.showToast 的扩展提示") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("显示 Toast 扩展") }
    }
}

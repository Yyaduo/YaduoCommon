package com.yaduo.commontest.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yaduo.common.applogic.AppLogicUtil

/**
 * ### 公共模块测试页面
 *
 * 展示 [AppLogicUtil] 管理的模块初始化状态。
 */
@Composable
fun CommonModuleScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "AppLogicUtil 状态", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "是否已初始化: ${AppLogicUtil.isInitialized}")
                    Text(text = "包名: ${AppLogicUtil.getApp().packageName}")
                    Text(text = "VersionCode: ${AppLogicUtil.getVersionCode()}")
                }
            }
        }

        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "已注册模块列表", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "- AutoSizeConfig")
                    Text(text = "- BuglyReport")
                    Text(text = "- Chucker")
                    Text(text = "- MMKV")
                }
            }
        }
    }
}

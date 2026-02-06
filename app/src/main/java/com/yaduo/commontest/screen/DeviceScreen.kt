package com.yaduo.commontest.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yaduo.common.device.DeviceInfo

/**
 * ### 设备信息测试页面
 *
 * 解析并展示 [DeviceInfo] 提供的各种硬件与系统参数。
 */
@Composable
fun DeviceScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "核心标识符", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "UUID (Build.SERIAL): ${DeviceInfo.getUuid()}")
                    Text(text = "UID (Gen by SHA-256): ${DeviceInfo.getUid()}")
                }
            }
        }

        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "实时状态", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "网络是否连接: ${DeviceInfo.isNetworkConnected()}")
                    Text(text = "是否为竖屏布局: ${DeviceInfo.isVisualPortrait()}")
                }
            }
        }

        item {
            Button(onClick = { DeviceInfo.printDeviceInfo() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "向 Logcat 打印完整信息")
            }
        }
    }
}

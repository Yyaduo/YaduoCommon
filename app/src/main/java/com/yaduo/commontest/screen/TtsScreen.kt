package com.yaduo.commontest.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yaduo.common.tts.TTSManager
import com.yaduo.common.tts.TtsEngineType

/** ### TTS 测试页面 */
@Composable
fun TtsScreen() {
    var textToSpeak by remember { mutableStateOf("Ciallo～(∠・ω<)⌒★，欢迎测试 YaduoCommon 文字转语音功能") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            OutlinedTextField(
                value = textToSpeak,
                onValueChange = { textToSpeak = it },
                label = { Text("待播报文本") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { TTSManager.speak(textToSpeak) },
                    modifier = Modifier.weight(1f)
                ) { Text("立即播报") }
                Button(onClick = { TTSManager.stop() }, modifier = Modifier.weight(1f)) {
                    Text("停止")
                }
            }
        }

        item {
            Divider()
            Text(text = "引擎切换", style = MaterialTheme.typography.titleSmall)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TtsEngineType.entries.forEach { type ->
                    val isCurrent = TTSManager.getCurrentEngineType() == type
                    FilterChip(
                        selected = isCurrent,
                        onClick = { TTSManager.switchEngine(type) },
                        label = { Text(type.name) }
                    )
                }
            }
        }

        item {
            Card {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "注：切换至 AZURE 需提前通过 configAzure 配置 Key")
                }
            }
        }
    }
}

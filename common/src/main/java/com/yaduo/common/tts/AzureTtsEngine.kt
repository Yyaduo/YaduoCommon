package com.yaduo.common.tts

import android.content.Context
import com.microsoft.cognitiveservices.speech.*
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import com.yaduo.common.log.LogUtil
import com.yaduo.common.util.MetaDataUtils
import kotlinx.coroutines.*

/**
 * ### Azure 官方 SDK TTS 引擎实现
 *
 * 集成微软 Azure Cognitive Services 语音合成功能。 优势在于提供高质量、多音色的神经网络语音，但需要网络连接。
 *
 * #### 🛠 集成指引
 * 1. **权限声明**：确保在 `AndroidManifest.xml` 中声明了网络权限。
 * ```xml
 *    <uses-permission android:name="android.permission.INTERNET" />
 *    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * ```
 *
 * 2. **凭据配置**：
 * - 在主工程的 `AndroidManifest.xml` 中配置 `<meta-data>`：
 * ```xml
 *   <meta-data android:name="KEY_AZURE" android:value="你的Key" />
 *   <meta-data android:name="KEY_AZURE_REGION" android:value="你的Region" />
 * ```
 *
 * 3. **引擎切换**：调用 `TTSManager.switchEngine(TtsEngineType.AZURE)`。
 *
 * @author YaDuo
 * @since 2026-02-05 16:36:12
 * @property context Android 上下文
 */
class AzureTtsEngine(private val context: Context) : ITtsEngine {

    private var speechConfig: SpeechConfig? = null
    private var synthesizer: SpeechSynthesizer? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isInitialized = false

    companion object {
        private const val TAG = "AzureTtsEngine"
        private const val META_KEY_AZURE = "KEY_AZURE"
        private const val META_KEY_AZURE_REGION = "KEY_AZURE_REGION"

        private var SPEECH_KEY = "YOUR_AZURE_SPEECH_KEY"
        private var SPEECH_REGION = "YOUR_AZURE_SERVICE_REGION"

        /** 默认使用的神经网络音色：晓晓 */
        private const val DEFAULT_VOICE = "zh-CN-XiaoxiaoNeural"
    }

    override fun getType(): TtsEngineType = TtsEngineType.AZURE

    /** 初始化 Azure SDK 必须在调用前通过AndroidManifest.xml 中配置有效的 API Key。 */
    override fun initialize() {
        SPEECH_KEY = MetaDataUtils.getMetaDataString(context, META_KEY_AZURE)
        SPEECH_REGION = MetaDataUtils.getMetaDataString(context, META_KEY_AZURE_REGION)
        if (SPEECH_KEY == "YOUR_AZURE_SPEECH_KEY") {
            LogUtil.w(TAG, "Azure Key 未正确配置（仍为占位符），跳过初始化")
            return
        }

        try {
            LogUtil.i(TAG, "正在初始化 Azure 语音合成引擎...，选择的发音人为：$DEFAULT_VOICE")
            speechConfig =
                SpeechConfig.fromSubscription(SPEECH_KEY, SPEECH_REGION).apply {
                    speechSynthesisVoiceName = DEFAULT_VOICE
                    // 设置音频输出格式为 MP3，平衡质量与带宽
                    setSpeechSynthesisOutputFormat(
                        SpeechSynthesisOutputFormat.Audio24Khz48KBitRateMonoMp3
                    )
                }
            val audioConfig = AudioConfig.fromDefaultSpeakerOutput()
            synthesizer = SpeechSynthesizer(speechConfig, audioConfig)
            isInitialized = true
            LogUtil.i(TAG, "Azure 引擎初始化完毕")
        } catch (e: Exception) {
            LogUtil.e(TAG, "Azure 引擎创建失败", e)
        }
    }

    /**
     * 播报文本
     *
     * @param text 待转码播报的文本
     * @param voice 指定 Azure 神经网络音色名（如 zh-CN-YunxiNeural）
     */
    override fun speak(text: String, voice: String?) {
        if (!isInitialized) {
            LogUtil.w(TAG, "speak 指令被忽略：Azure 引擎未就绪")
            return
        }
        val currentSynthesizer = synthesizer ?: return

        LogUtil.i(TAG, "正在异步播报 (Azure): $text")
        scope.launch {
            try {
                // SpeakTextAsync 为非阻塞调用，使用 .get() 监听当前句播报结束
                val result = currentSynthesizer.SpeakTextAsync(text).get()
                if (result.reason == ResultReason.Canceled) {
                    val cancellation = SpeechSynthesisCancellationDetails.fromResult(result)
                    LogUtil.e(TAG, "Azure 播报被动态取消: ${cancellation.errorDetails}")
                } else if (result.reason == ResultReason.SynthesizingAudioCompleted) {
                    LogUtil.i(TAG, "Azure 播报顺利完成")
                }
            } catch (e: Exception) {
                LogUtil.e(TAG, "Azure IO 协程异常", e)
            }
        }
    }

    /** 强行中断当前 Azure 播放 */
    override fun stop() {
        try {
            LogUtil.i(TAG, "指令：停止 Azure 语音合成")
            synthesizer?.StopSpeakingAsync()?.get()
        } catch (e: Exception) {
            LogUtil.e(TAG, "停止 Azure 任务异常", e)
        }
    }

    /** 释放 Azure SDK 及其协程域占用的所有资源 */
    override fun release() {
        LogUtil.i(TAG, "准备释放 Azure 引擎资源")
        stop()
        synthesizer?.close()
        speechConfig?.close()
        scope.cancel()
        isInitialized = false
    }

    override fun isReady(): Boolean = isInitialized
}

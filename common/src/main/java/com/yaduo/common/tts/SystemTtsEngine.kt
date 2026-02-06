package com.yaduo.common.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.yaduo.common.log.LogUtil
import java.util.*

/**
 * ### 系统原生 TTS 引擎实现
 *
 * 基于 Android 标准库中的 [android.speech.tts.TextToSpeech] 开发。 优势在于无需引入第三方库且支持离线播报（视具体设备 TTS 引擎而定）。
 *
 * @property context 用于初始化 TextToSpeech 的 Android 上下文
 * @author YaDuo
 * @since 2026-02-05 16:36:12
 */
class SystemTtsEngine(private val context: Context) : ITtsEngine, TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    companion object {
        private const val TAG = "SystemTtsEngine"
    }

    override fun getType(): TtsEngineType = TtsEngineType.SYSTEM

    /** 执行系统 TTS 引擎的具体初始化 通过 [TextToSpeech] 构造方法建立连接，成功后会回调 [onInit]。 */
    override fun initialize() {
        LogUtil.i(TAG, "正在初始化系统原生 TTS 引擎...")
        tts = TextToSpeech(context, this)
    }

    /**
     * [TextToSpeech.OnInitListener] 回调接口 接管引擎初始化状态，并配置默认语言和播报进度监听。
     *
     * @param status 初始化结果，[TextToSpeech.SUCCESS] 代表成功
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            var result = tts?.setLanguage(Locale.SIMPLIFIED_CHINESE)
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                result = tts?.setLanguage(Locale.CHINESE)
            }
            tts?.apply {
                setPitch(1.0f)
                setSpeechRate(1.1f)
                setOnUtteranceProgressListener(
                    object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            LogUtil.i(TAG, "播报开始: $utteranceId")
                        }

                        override fun onDone(utteranceId: String?) {
                            LogUtil.i(TAG, "播报完成: $utteranceId")
                        }

                        override fun onError(utteranceId: String?) {
                            LogUtil.e(TAG, "播报错误: $utteranceId")
                        }
                    }
                )
            }

            isInitialized = true
            LogUtil.i(TAG, "系统原生 TTS 引擎连接成功")
        } else {
            LogUtil.e(TAG, "系统原生 TTS 引擎初始化失败，状态码: $status")
        }
    }

    /**
     * 执行文本播报任务
     *
     * @param text 需要转换为语音的文本内容
     * @param voice 音色标记（系统引擎当前版本会忽略此参数）
     */
    override fun speak(text: String, voice: String?) {
        if (!isInitialized) {
            LogUtil.w(TAG, "speak 终止: 引擎尚未就绪")
            return
        }
        val utteranceId = "system_${System.currentTimeMillis()}"
        LogUtil.i(TAG, "正在通过系统引擎播报: $text")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    /** 强行中断播放中的语音 */
    override fun stop() {
        LogUtil.i(TAG, "系统引擎停止播报")
        tts?.stop()
    }

    /** 释放 TextToSpeech 资源 开发规范建议在组件生命周期销毁时显式调用 */
    override fun release() {
        LogUtil.i(TAG, "释放系统 TTS 引擎资源")
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }

    override fun isReady(): Boolean = isInitialized
}

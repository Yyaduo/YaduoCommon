package com.yaduo.common.tts

import com.yaduo.common.applogic.AppLogicUtil
import com.yaduo.common.log.LogUtil

/**
 * ### TTS 功能统一管理中心 (SDK Entry)
 *
 * 封装了多种 TTS 引擎的生命周期管理与切换逻辑。 外部业务方仅需关注 [TTSManager] 提供的接口，无需感知引擎的具体实现细节。
 *
 * #### 💡 快速使用示例
 * 1. 在 Application 中初始化
 * ```kotlin
 * TTSManager.init(this)
 * ```
 * 2. 播报默认语音
 * ```kotlin
 * TTSManager.speak("欢迎使用 YaduoCommon")
 * ```
 * 3. 动态切换引擎
 * ```kotlin
 * // 切换至 Azure 神经网络语音
 * TTSManager.configAzure("your_key", "your_region")
 * TTSManager.switchEngine(TtsEngineType.AZURE)
 * ```
 *
 * @author YaDuo
 * @since 2026-02-05 16:36:12
 */
object TTSManager {

    private const val TAG = "TTSManager"

    /** 当前使用的引擎 */
    private var currentEngine: ITtsEngine? = null
    private var currentEngineType: TtsEngineType = TtsEngineType.SYSTEM

    /**
     * 初始化 TTS 全局配置
     *
     * @param defaultType 首次加载的引擎类型，默认 [TtsEngineType.SYSTEM]
     */
    fun init(defaultType: TtsEngineType = TtsEngineType.SYSTEM) {
        this.currentEngineType = defaultType
        LogUtil.i(TAG, "TTSManager init: Default=$defaultType")
        switchEngine(defaultType)
    }

    /**
     * 按类型动态切换底层执行引擎
     *
     * 会自动释放旧引擎资源并初始化新引擎。
     *
     * @param type 目标类型 [TtsEngineType]
     */
    fun switchEngine(type: TtsEngineType) {
        val ctx = AppLogicUtil.getApp()

        if (currentEngine?.getType() == type) {
            LogUtil.i(TAG, "引擎类型一致，无需重复切换 ($type)")
            return
        }

        LogUtil.i(TAG, "执行引擎切换任务: 目标=$type")
        currentEngine?.release()

        currentEngine = when (type) {
            TtsEngineType.SYSTEM -> SystemTtsEngine(ctx)
            TtsEngineType.AZURE -> AzureTtsEngine(ctx)
        }

        currentEngine?.initialize()
        currentEngineType = type
    }

    /** 获取当前活动引擎的类型 */
    fun getCurrentEngineType(): TtsEngineType = currentEngineType

    /**
     * 播报文本
     *
     * @param text 待播报内容
     * @param voice 特殊音色指定
     */
    fun speak(text: String, voice: String? = null) {
        currentEngine?.let {
            if (it.isReady()) {
                LogUtil.i(TAG, "TTS 任务下发: $text")
                it.speak(text, voice)
            } else {
                LogUtil.w(TAG, "引擎尚未就绪，尝试二次初始化并播报")
                it.initialize()
                it.speak(text, voice)
            }
        } ?: LogUtil.e(TAG, "speak 指令失败: 尚未配置任何引擎")
    }

    /** 停止当前进行的播报 */
    fun stop() {
        LogUtil.i(TAG, "接收到全局停止播报请求")
        currentEngine?.stop()
    }

    /** 彻底关停 TTS 服务并回收上下文引用 */
    fun release() {
        LogUtil.i(TAG, "回收 TTSManager 全局资源")
        currentEngine?.release()
        currentEngine = null
    }
}

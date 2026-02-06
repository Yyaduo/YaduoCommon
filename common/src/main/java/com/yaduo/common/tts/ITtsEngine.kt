package com.yaduo.common.tts

/**
 * ### TTS 引擎通用接口定义
 *
 * 定义了文字转语音（Text-To-Speech）引擎需要实现的核心行为。 不同的底层 SDK（如系统原生、Azure SDK）通过实现此接口提供统一的能力。
 *
 * @author YaDuo
 * @since 2026-02-05 16:36:12
 */
interface ITtsEngine {
    /**
     * 获取引擎类型
     * @return 引擎类型 [TtsEngineType]
     */
    fun getType(): TtsEngineType

    /** 引擎初始化逻辑 实现类应在此处进行 SDK 的配置与连接。 */
    fun initialize()

    /**
     * 播报指定的文本内容
     * @param text 待播报文本字符串
     * @param voice 指定音色标识符（若引擎支持则应用，否则使用引擎内部默认值）
     */
    fun speak(text: String, voice: String? = null)

    /** 强行停止当前的播报任务 */
    fun stop()

    /** 释放引擎占用的资源 调用后引擎将进入不可用状态，若需再次使用通常需重新调用 [initialize]。 */
    fun release()

    /**
     * 检查引擎是否已就绪并可接收播报指令
     * @return true 表示已就绪，false 表示未初始化或已释放
     */
    fun isReady(): Boolean
}

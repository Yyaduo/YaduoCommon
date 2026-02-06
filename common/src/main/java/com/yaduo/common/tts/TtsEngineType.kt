package com.yaduo.common.tts

/**
 * ### TTS 引擎类型枚举
 *
 * 用于标识和切换不同的 Text-To-Speech 服务提供商。
 *
 * @author YaDuo
 * @since 2026-02-05 16:36:12
 */
enum class TtsEngineType {
    /** Android 系统自带的原生 TTS 服务 */
    SYSTEM,
    /** 微软 Azure Cognitive Services 官方 SDK 服务 */
    AZURE,
}

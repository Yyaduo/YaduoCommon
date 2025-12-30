package com.yaduo.common.extra

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.yaduo.common.log.LogUtil

/**
 * ProcessLifecycleOwner的空安全扩展方法
 * 解决API层面的null风险
 */
fun ProcessLifecycleOwner.Companion.getOrNull(): LifecycleOwner? {
    return try {
        ProcessLifecycleOwner.get()
    } catch (e: Exception) {
        LogUtil.e(content = "获取ProcessLifecycleOwner失败", throwable = e)
        null
    }
}
package com.yaduo.common.extra

import android.content.Context
import android.widget.Toast
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

/**
 * 在当前上下文（Context）中显示一个短时长的 Toast 提示消息。
 *
 * @param message 要显示的文本消息
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * 显示一个短时长的 Toast 提示消息。
 *
 * @param message 字符串资源的 ID，用于获取要显示的消息内容
 */
fun Context.showToast(message: Int) {
    val content = this.resources.getString(message)
    this.showToast(content)
}